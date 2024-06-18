package com.jo.excel;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.jo.common.core.util.WebUtils;
import com.jo.excel.annotation.ExcelFileProperty;
import com.jo.excel.handler.ExcelColumnHandler;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * excel导出工具类
 * @author xtc
 * @date 2024/6/7
 */
@SuppressWarnings("unused")
public class EasyExcelUtil {

    /**
     * 导出Excel 多sheet
     * @param data 数据
     * @param fileName 文件名
     * @param <T> <Object>
     */
    public static <T> void exportMultiSheet(List<List<T>> data,String fileName)  {
        ExcelWriter excelWriter = null;
        HttpServletResponse response = WebUtils.getResponse();
        try {

            setExcelResponseProp(response,fileName);

            excelWriter = EasyExcel.write(response.getOutputStream()).build();

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) == null) {
                    throw new RuntimeException("导出失败");
                }
                List<T> d = data.get(i);
                T t = genericSuperClass(data.get(i));
                Class<?> aClass = t.getClass();

                ExcelFileProperty annotation = aClass.getAnnotation(ExcelFileProperty.class);
                String sheetName = annotation.sheet().equals("Sheet") ? "Sheet" + (i + 1): annotation.sheet();

                WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).head(aClass).build();
                excelWriter.write(d, writeSheet);

            }
            excelWriter.finish();

        } catch (IOException e) {
            throw new RuntimeException("导出失败");
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
            closeResponse(response);
        }
    }


    /**
     * 导出Excel
     * @param data 数据
     * @param <T> <T>
     */
    public static <T> void export(List<T> data){
        HttpServletResponse response = WebUtils.getResponse();
        try {
            T t = genericSuperClass(data);

            Class<?> aClass = t.getClass();
            ExcelFileProperty annotation = aClass.getAnnotation(ExcelFileProperty.class);

            setExcelResponseProp(response,annotation.fileName());
            EasyExcel.write(response.getOutputStream())
                    .head(aClass)
                    .registerWriteHandler(new ExcelColumnHandler()) // 自动列宽
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet(annotation.sheet())
                    .doWrite(data);
        } catch (IOException e) {
            throw new RuntimeException("导出失败");
        } finally {
            closeResponse(response);
        }

    }

    private static void closeResponse(HttpServletResponse response) {
        try {
            response.getOutputStream().close();
        } catch (IOException e) {
            throw new RuntimeException("导出失败");
        }
    }


    private static <T> T genericSuperClass(List<T> data){
        if (CollectionUtils.isEmpty(data)){
            throw new RuntimeException("导出失败");
        }
        return data.stream()
                .filter(Objects::nonNull)
                .findAny()
                .orElseThrow(() -> new RuntimeException("导出失败"));
    }


    /**
     * 设置响应结果
     *
     * @param response    响应结果对象
     * @param rawFileName 文件名
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    private static void setExcelResponseProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String fileName = URLEncoder.encode(rawFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }


}
