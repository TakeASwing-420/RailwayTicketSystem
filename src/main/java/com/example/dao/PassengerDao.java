package com.example.dao;

import com.example.model.Passenger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class PassengerDao {
    private static final String PASSENGER_TICKETS_FILE = "data/passenger_tickets.xlsx";
    private int serialNumber = 1;

    public PassengerDao() {
        initializeSerialNumber();
    }

    private void initializeSerialNumber() {
        try (FileInputStream fis = new FileInputStream(PASSENGER_TICKETS_FILE);
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

    public void savePassenger(Passenger passenger) throws IOException {
        try (FileInputStream fis = new FileInputStream(PASSENGER_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            
            row.createCell(0).setCellValue(serialNumber++);
            row.createCell(1).setCellValue(passenger.getName());
            row.createCell(2).setCellValue(passenger.getTrainNumber());
            row.createCell(3).setCellValue(passenger.getTicketPrice());
            row.createCell(4).setCellValue(passenger.getAge());
            row.createCell(5).setCellValue(passenger.getGender());
            row.createCell(6).setCellValue(passenger.getStatus());
            
            try (FileOutputStream fos = new FileOutputStream(PASSENGER_TICKETS_FILE)) {
                workbook.write(fos);
            }
        }
    }

    public List<Passenger> getAllPassengers() throws IOException {
        List<Passenger> passengers = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(PASSENGER_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Passenger passenger = new Passenger(
                    getCellValue(row.getCell(1)),
                    Integer.parseInt(getCellValue(row.getCell(4))),
                    getCellValue(row.getCell(5)),
                    getCellValue(row.getCell(6)),
                    getCellValue(row.getCell(2)),
                    Integer.parseInt(getCellValue(row.getCell(3)))
                );
                passenger.setSerialNumber(Integer.parseInt(getCellValue(row.getCell(0))));
                passengers.add(passenger);
            }
        }
        return passengers;
    }

    public void deletePassenger(int serialNumber) throws IOException {
        File file = new File(PASSENGER_TICKETS_FILE);
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(0)).equals(String.valueOf(serialNumber))) {
                    removeRow(sheet, row.getRowNum());
                    break;
                }
            }
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    private void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
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
