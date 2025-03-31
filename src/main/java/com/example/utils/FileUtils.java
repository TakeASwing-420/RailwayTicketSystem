package com.example.utils;

import java.io.File;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

public class FileUtils {
    public static void initializeFiles() throws IOException {
        // Create data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        // Initialize Excel files with headers if they don't exist
        initializeExcelFile("data/data.xlsx", 
            new String[]{"ID", "Train Number", "From", "To", "Train Name", "Arrival", "Departure", 
                         "Type", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Available Seats"});
        
        initializeExcelFile("data/passenger_tickets.xlsx", 
            new String[]{"Serial No", "Name", "Train Number", "Ticket Price", "Age", "Gender", "Status"});
            
        initializeExcelFile("data/platform_tickets.xlsx", 
            new String[]{"Serial No", "Train Number", "Price"});
    }

    private static void initializeExcelFile(String filePath, String[] headers) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Data");
                Row headerRow = sheet.createRow(0);
                
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }
                
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                    workbook.write(fos);
                }
            }
        }
    }
}
