package com.jo.common.captcha;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.openqa.selenium.interactions.PointerInput.Kind.MOUSE;


public class SildeCode {

    private BufferedImage imgBase;
    private BufferedImage imgSilder;

    public void d(ChromeDriver driver,WebElement element){

        try {
            Actions actions = new Actions(driver);
//            int left = getGap(baseImage,silderImage);
//            left -= 7;
            Mat baseImg = baseImgProcess();
            Mat silderImg = smallImgProcess();
            Mat result = new Mat();
            DoublePointer minVal= new DoublePointer();
            DoublePointer maxVal= new DoublePointer();
            Point min = new Point();
            Point max = new Point();
            // 匹配
            matchTemplate(baseImg, silderImg, result, TM_CCOEFF_NORMED);
            // 获取结果
            minMaxLoc(result, minVal, maxVal, min, max, null);
            // 打印移动距离
            System.out.println(max.x());

            int left = max.x();

            List<Integer> trace = trace(left);
            trace.forEach(System.out::println);
            move(trace,element,driver,actions);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


//    public int getGap(File baseImage,File silderImage) throws Exception {
//        imgBase = ImageIO.read(baseImage);
//        imgSilder = ImageIO.read(silderImage);
//        int width = imgBase.getWidth();
//        int height = imgBase.getHeight();
//        int pos = 60;
//        // 横向扫描
//        for (int i = pos; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                if (!equalPixel(i, j)) {
//                    pos = i;
//                    return pos;
//                }
//            }
//        }
//        throw new Exception("未找到滑块缺口");
//    }
//    /**
//     * 比较两张截图上的当前像素点的RGB值是否相同
//     * 只要满足一定误差阈值，便可认为这两个像素点是相同的
//     *
//     * @param x 像素点的x坐标
//     * @param y 像素点的y坐标
//     * @return true/false
//     */
//    public boolean equalPixel(int x, int y) {
//        int rgbaBase = imgBase.getRGB(x, y);
//        int rgbaSilder = imgSilder.getRGB(x, y);
//        // 转化成RGB集合
//        Color colBase = new Color(rgbaBase, true);
//        Color colSilder = new Color(rgbaSilder, true);
//        int threshold = 80;   // RGB差值阈值
//        if (Math.abs(colBase.getRed() - colSilder.getRed()) < threshold &&
//                Math.abs(colBase.getGreen() - colSilder.getGreen()) < threshold &&
//                Math.abs(colBase.getBlue() - colSilder.getBlue()) < threshold) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 计算滑块到达目标点的运行轨迹
     * 先加速，后减速
     * @param distance 目标距离
     * @return 运动轨迹
     */
    public List<Integer> trace(int distance) {
        List<Integer> moveTrace = new ArrayList<>();
        int current = 0;  // 当前位移
        int threshold = distance * 3 / 5; // 减速阈值
        double t = 0.2;   // 计算间隔
        double v = 0.0;     // 初速度
        double a;     // 加速度
        while (current < distance) {
            if (current < threshold) {
                a = 2;
            } else {
                a = -4;
            }
            // 位移计算公式
            double tmp = v;
            // 移动速度，会出现负值的情况，然后往反方向拉取
            v = tmp + a * t;
            int move = (int) (tmp * t + 0.5 * a * t * t);
            current += move;
            moveTrace.add(move);
        }
        // 考虑到最后一次会超出移动距离，将其强制修改回来，不允许超出
        int length = moveTrace.size();
        moveTrace.set(length - 1, moveTrace.get(length - 1) + (current > distance ? -(current - distance) : 0));
        return moveTrace;
    }

    /**
     * 消除selenium中移动操作的卡顿感
     * 这种卡顿感是因为selenium中自带的moveByOffset是默认有200ms的延时的
     *
     * @param x x轴方向位移距离
     * @param y y轴方向位移距离
     */
    public void moveWithoutWait(int x, int y,Actions actions) {
        PointerInput defaultMouse = new PointerInput(MOUSE, "default mouse");
        actions.tick(defaultMouse.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.pointer(), x, y)).perform();
    }


    /**
     * 移动滑块，实现验证
     * @param moveTrace 滑块的运动轨迹
     * @throws Exception
     */
    public void move(List<Integer> moveTrace, WebElement element,ChromeDriver driver,Actions actions) throws Exception {
        // 按下滑块
        actions.clickAndHold(element).perform();
        Iterator it = moveTrace.iterator();
        while (it.hasNext()) {
            // 位移一次
            int dis = (int) it.next();
            moveWithoutWait(dis, 0,actions);
        }
        // 模拟人的操作，超过区域
        moveWithoutWait(5, 0,actions);
        moveWithoutWait(-3, 0,actions);
        moveWithoutWait(-2, 0,actions);
        // 释放滑块
        actions.release().perform();
        Thread.sleep(500);
    }


    private static Mat baseImgProcess(){
        Mat baseImage = imread("d:/before1.png");
        // 1、灰度化图片
        cvtColor(baseImage, baseImage, COLOR_RGB2GRAY);
        // 2、二值化转黑白图
        // adaptiveThreshold(bigImg, bigImg,255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY_INV, 3, 20);
        threshold(baseImage, baseImage, 140, 255, THRESH_BINARY);
        imwrite("big.jpg", baseImage);
        return baseImage;
    }

    /**
     *  小滑块的处理
     * @return
     */
    private static Mat smallImgProcess() {
        Mat smallImg = imread("d:/after1.png");
        if (smallImg.empty()) {
            return null;
        }
        // 1、灰度化图片
        cvtColor(smallImg, smallImg, COLOR_BGR2GRAY);
        int width= smallImg.rows();
        int height = smallImg.cols();
        // 2、去除周围黑边
        UByteRawIndexer smallImgIndexer = smallImg.createIndexer();
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                if (smallImgIndexer.get(row, col) == 0) {
                    smallImgIndexer.put(row, col, 255);
                }
            }
        }
        smallImgIndexer.release();
        // 3、inRange二值化转黑白图
        Mat newMat = new Mat(width,height, CV_8UC1, Scalar.all(255));
        inRange(smallImg, newMat, newMat, smallImg);
        imwrite("small.png", smallImg);
        return smallImg;
    }

}
