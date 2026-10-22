package ru.mirea.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.mirea.model.WasteRequest;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {

    public static void exportRequests(List<WasteRequest> requests, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Заявки");
            Row headerRow = sheet.createRow(0);
            
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("ID Клиента");
            headerRow.createCell(2).setCellValue("Адрес");
            headerRow.createCell(3).setCellValue("Тип отходов");
            headerRow.createCell(4).setCellValue("Объем (м3)");
            headerRow.createCell(5).setCellValue("Статус");
            
            int rowNum = 1;
            for (WasteRequest req : requests) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(req.getId());
                row.createCell(1).setCellValue(req.getClientId());
                row.createCell(2).setCellValue(req.getAddress());
                row.createCell(3).setCellValue(req.getWasteType());
                row.createCell(4).setCellValue(req.getVolumeM3());
                row.createCell(5).setCellValue(req.getStatus().name());
            }
            
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при экспорте в Excel", e);
        }
    }
}