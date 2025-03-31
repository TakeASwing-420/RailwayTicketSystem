package com.example.dao;

import com.example.model.Train;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class TrainDao {
    private static final String TRAIN_DATA_FILE = "data/data.xlsx";

    public List<Train> getAllTrains() throws IOException {
        List<Train> trains = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                trains.add(new Train(
                    getCellValue(row.getCell(1)),
                    getCellValue(row.getCell(2)),
                    getCellValue(row.getCell(3)),
                    getCellValue(row.getCell(6)),
                    getCellValue(row.getCell(5)),
                    getCellValue(row.getCell(7)),
                    Integer.parseInt(getCellValue(row.getCell(15)))
                ));
            }
        }
        return trains;
    }

    public Train getTrainByNumber(String trainNumber) throws IOException {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(1)).equals(trainNumber)) {
                    return new Train(
                        trainNumber,
                        getCellValue(row.getCell(2)),
                        getCellValue(row.getCell(3)),
                        getCellValue(row.getCell(6)),
                        getCellValue(row.getCell(5)),
                        getCellValue(row.getCell(7)),
                        Integer.parseInt(getCellValue(row.getCell(15)))
                    );
                }
            }
        }
        return null;
    }

    public void updateSeats(String trainNumber, int seats) throws IOException {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(1)).equals(trainNumber)) {
                    row.getCell(15).setCellValue(seats);
                    break;
                }
            }
            
            try (FileOutputStream fos = new FileOutputStream(TRAIN_DATA_FILE)) {
                workbook.write(fos);
            }
        }
    }

    public void addNewTrain(String trainNumber, String from, String to, 
                          String departure, String arrival, String type, int seats) throws IOException {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            
            newRow.createCell(0).setCellValue(sheet.getLastRowNum());
            newRow.createCell(1).setCellValue(trainNumber);
            newRow.createCell(2).setCellValue(from);
            newRow.createCell(3).setCellValue(to);
            newRow.createCell(4).setCellValue(trainNumber);
            newRow.createCell(5).setCellValue(arrival);
            newRow.createCell(6).setCellValue(departure);
            newRow.createCell(7).setCellValue(type);
            newRow.createCell(8).setCellValue("Yes");
            newRow.createCell(9).setCellValue("No");
            newRow.createCell(10).setCellValue("No");
            newRow.createCell(11).setCellValue("Yes");
            newRow.createCell(12).setCellValue("No");
            newRow.createCell(13).setCellValue("Yes");
            newRow.createCell(14).setCellValue("No");
            newRow.createCell(15).setCellValue(seats);

            try (FileOutputStream fos = new FileOutputStream(TRAIN_DATA_FILE)) {
                workbook.write(fos);
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
