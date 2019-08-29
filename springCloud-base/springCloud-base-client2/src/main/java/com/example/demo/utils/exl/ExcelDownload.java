package com.example.demo.utils.exl;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Random;

@Slf4j
@Controller
@RequestMapping("excel")
public class ExcelDownload {

    /**
     * 测试Excel下载 xlsx类型 每个sheet 100w左右
     */
    @RequestMapping("/xlsx")
    public void xlsx(HttpServletResponse response) {
        System.out.println(">>> excel");
        long start = System.currentTimeMillis();
        try (OutputStream out = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String("测试excel.xlsx".getBytes(), "ISO8859-1"));

            SXSSFWorkbook workbook = new SXSSFWorkbook();
            SXSSFSheet sheet = workbook.createSheet("测试");
            Random ran = new Random();
            SXSSFRow row;
            SXSSFCell cell;
            for (int i = 0; i < 100000; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 50; j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(ran.nextInt(Integer.MAX_VALUE));
                }
            }
            workbook.write(out);
            workbook.close();
        } catch (Exception e) {
            log.error("--- excel 异常 --- {}", e);
        }

        long end = System.currentTimeMillis();
        System.out.println("<><> End " + (end - start) + "ms");
    }

    /**
     * 测试Excel下载 xls类型 每个sheet 6w左右
     */
    @RequestMapping("/xls")
    public void xls(HttpServletResponse response) {
        System.out.println(">>> excel");
        long start = System.currentTimeMillis();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filiename=" + new String("测试excel"));

        try (OutputStream out = response.getOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet("测试", 0);
            Random ran = new Random();
            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < 100000; j++) {
                    sheet.addCell(new Label(i, j, "" + ran.nextInt(10)));
                }
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            log.error("--- excel 异常 --- {}", e);
        }

        long end = System.currentTimeMillis();
        System.out.println("<><> End " + (end - start) + "ms");
    }
}
