package com.example.dao;
import com.example.model.PlatformTicket; 
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class PlatformTicketDao {
    private static final String PLATFORM_TICKETS_FILE = "data/platform_tickets.xlsx";
    private int serialNumber = 1;

    public PlatformTicketDao() {
        initializeSerialNumber();
    }

    private void initializeSerialNumber() {
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            serialNumber = sheet.getLastRowNum() + 1;
        } catch (Exception e) {
            serialNumber = 1;
        }
    }

    public void resetSerialNumber() {
        this.serialNumber = 1;
    }

    public void saveTicket(String trainNumber, int price) throws IOException {
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            
            row.createCell(0).setCellValue(serialNumber++);
            row.createCell(1).setCellValue(trainNumber);
            row.createCell(2).setCellValue(price);
            
            try (FileOutputStream fos = new FileOutputStream(PLATFORM_TICKETS_FILE)) {
                workbook.write(fos);
            }
        }
    }

    public List<Map<String, String>> getTicketsByTrain(String trainNumber) throws IOException {
        List<Map<String, String>> tickets = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(1)).equals(trainNumber)) {
                    Map<String, String> ticket = new HashMap<>();
                    ticket.put("serial", getCellValue(row.getCell(0)));
                    ticket.put("price", getCellValue(row.getCell(2)));
                    tickets.add(ticket);
                }
            }
        }
        return tickets;
    }

    public int getTicketCountForTrain(String trainNumber) throws IOException {
        int count = 0;
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(1)).equals(trainNumber)) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<PlatformTicket> getAllTickets() throws IOException {
        List<PlatformTicket> tickets = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                PlatformTicket ticket = new PlatformTicket(
                    getCellValue(row.getCell(1)),
                    Integer.parseInt(getCellValue(row.getCell(2)))
                );
                ticket.setSerialNumber(Integer.parseInt(getCellValue(row.getCell(0))));
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int)cell.getNumericCellValue());
            default: return "";
        }
    }
}
