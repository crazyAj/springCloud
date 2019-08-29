package com.example.demo.utils.exl;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * 测试exl工具
 */
public class JXL {

    @Test
    public void testExl() {
        WritableWorkbook workbook = null;
        try {
            String path = this.getClass().getClassLoader().getResource("exl/t.xls").getPath();
            if (path.indexOf("target") != -1) {
                path = path.replace(path.substring(path.indexOf("target"), path.indexOf("exl/t.xls")),"src/main/resources/");
            }
            System.out.println(path);
            File exl = new File(path);
            workbook = Workbook.createWorkbook(exl);
            WritableSheet sheet = workbook.createSheet("第一", 0);
            sheet.addCell(new Label(0, 0, "0"));
            sheet.addCell(new Label(3, 2, "1"));
            sheet.addCell(new Label(0, 1, "2"));
            sheet.addCell(new Label(3, 3, "3"));
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null)
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
        }

    }

}
