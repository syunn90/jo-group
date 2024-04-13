package com.jo.sort;

import java.util.Arrays;

/**
 * 冒泡排序
 * @author xtc
 * @date 2024/4/13
 * @description
 * <p>
 *     冒泡排序（Bubble Sort）也是一种简单直观的排序算法。它重复地走访过要排序的数列，
 *     一次比较两个元素，如果他们的顺序错误就把他们交换过来。走访数列的工作是重复地进行直到没有再需要交换，也就是说该数列已经排序完成。
 * <p/>
 */
public class BubbleSort {

    public static int[] sort(int[] sourceArray)  {
        // 对 arr 进行拷贝，不改变参数内容
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

        for (int i = 1; i < arr.length; i++) {
            // 设定一个标记，若为true，则表示此次循环没有进行交换，也就是待排序列已经有序，排序已经完成。
            boolean flag = true;

            for (int j = 0; j < arr.length - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    flag = false;
                }
            }

            if (flag) {
                break;
            }
        }
        return arr;
    }

    public static void main(String[] args) {
        int[] sort = sort(new int[]{1, 2, 3, 4, 9, 6, 7, 8, 5});
    }

}
