package com.example;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class RailwayTicketSystemSwing {
    private static final Random random = new Random();
    
    // Ticket price ranges
    private static final int PLATFORM_TICKET_MIN = 10, PLATFORM_TICKET_MAX = 50;
    private static final int SUPERFAST_MIN = 1500, SUPERFAST_MAX = 2500;
    private static final int MAIL_MIN = 500, MAIL_MAX = 1500;
    private static final int LOCAL_MIN = 50, LOCAL_MAX = 500;
    private static String currentCommand;
    
    private static final String TRAIN_DATA_FILE = "./Train2/RailwayTicketSystem/data/data.xlsx";
private static final String PASSENGER_TICKETS_FILE = "./Train2/RailwayTicketSystem/data/passenger_tickets.xlsx";
private static final String PLATFORM_TICKETS_FILE = "./Train2/RailwayTicketSystem/data/platform_tickets.xlsx";

    // Counters and data structures
    private static int totalTicketsSold = 0;
    private static int serialNumberPassenger = 1;
    private static int serialNumberPlatform = 1;
    private static final Map<String, Integer> platformTickets = new HashMap<>();

    // Main frame and components
    private static JFrame mainFrame;
    private static JTextArea outputArea;
    private static JTextField inputField;
    private static JPasswordField passwordField;
    private static CardLayout cardLayout;
    private static JPanel cardPanel;

    // Passenger class for storing passenger information
    private static class Passenger {
        String name;
        int age;
        String gender;
        
        public Passenger(String name, int age, String gender) {
            this.name = name;
            this.age = age;
            this.gender = gender;
        }
    }

    public static void main(String[] args) {
        // Initialize files if they don't exist
        initializeFiles();
        
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void initializeFiles() {
        createFileIfNotExists(TRAIN_DATA_FILE, "Train Data");
        createFileIfNotExists(PASSENGER_TICKETS_FILE, "Passenger Tickets");
        createFileIfNotExists(PLATFORM_TICKETS_FILE, "Platform Tickets");
    }

    private static void createFileIfNotExists(String fileName, String sheetName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                workbook.createSheet(sheetName);
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.close();
            } catch (Exception e) {
                System.err.println("Error creating " + fileName + ": " + e.getMessage());
            }
        }
    }

    private static void createAndShowGUI() {
        // Create main frame
        mainFrame = new JFrame("Railway Ticket Booking System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLayout(new BorderLayout());

        // Create card layout for different screens
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create main menu panel
        JPanel mainMenuPanel = createMainMenuPanel();
        cardPanel.add(mainMenuPanel, "MainMenu");

        // Create password panel
        JPanel passwordPanel = createPasswordPanel();
        cardPanel.add(passwordPanel, "Password");

        // Create admin panel
        JPanel adminPanel = createAdminPanel();
        cardPanel.add(adminPanel, "Admin");

        // Create output area and input panel
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.addActionListener(e -> processInput(inputField.getText()));
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> processInput(inputField.getText()));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        // Add components to frame
        mainFrame.add(cardPanel, BorderLayout.NORTH);
        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(inputPanel, BorderLayout.SOUTH);

        // Show frame
        mainFrame.setVisible(true);
    }

    private static JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        String[] buttonLabels = {
            "Search by Train Number", "Search by Destination Station",
            "Check Seat Availability", "Check Platform Ticket Availability",
            "Book Platform Ticket", "Hourly Report",
            "Query Platform Tickets", "Admin Functions",
            "Book Passenger Ticket", "Exit"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(e -> handleMainMenuChoice(label));
            panel.add(button);
        }

        return panel;
    }

    private static JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        JLabel label = new JLabel("Enter Admin Password:");
        passwordField = new JPasswordField();
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back");
        
        submitButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            if (authenticate(password)) {
                cardLayout.show(cardPanel, "Admin");
                outputArea.append("✔️ Admin access granted.\n");
            } else {
                outputArea.append("❌ Incorrect password! Access denied.\n");
                cardLayout.show(cardPanel, "MainMenu");
            }
            passwordField.setText("");
        });
        
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "MainMenu");
            passwordField.setText("");
        });
        
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        
        panel.add(label);
        panel.add(passwordField);
        panel.add(buttonPanel);
        
        return panel;
    }

    private static JPanel createAdminPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        String[] buttonLabels = {
            "Generate Detailed Report", "Modify Train Data",
            "Delete Passenger Data", "Reset Ticket Counters",
            "Add New Train", "View All Trains",
            "View All Passengers", "View All Platform Tickets",
            "Back to Main Menu"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(e -> handleAdminChoice(label));
            panel.add(button);
        }

        return panel;
    }

    private static void handleMainMenuChoice(String choice) {
        outputArea.append("\n");
        switch (choice) {
            case "Search by Train Number":
                outputArea.append("Enter Train Number: ");
                currentCommand = "SearchByTrain";
                inputField.requestFocus();
                break;
            case "Search by Destination Station":
                outputArea.append("Enter Destination Station: ");
                currentCommand = "SearchByDestination";
                inputField.requestFocus();
                break;
            case "Check Seat Availability":
                outputArea.append("Enter Train Number to check seat availability: ");
                currentCommand = "CheckSeatAvailability";
                inputField.requestFocus();
                break;
            case "Check Platform Ticket Availability":
                outputArea.append("Enter Train Number to check platform ticket availability: ");
                currentCommand = "CheckPlatformAvailability";
                inputField.requestFocus();
                break;
            case "Book Platform Ticket":
                outputArea.append("Enter Train Number for Platform Ticket: ");
                currentCommand = "BookPlatformTicket";
                inputField.requestFocus();
                break;
            case "Hourly Report":
                generateHourlyReport();
                break;
            case "Query Platform Tickets":
                outputArea.append("Enter Train Number to check platform tickets: ");
                currentCommand = "QueryPlatformTickets";
                inputField.requestFocus();
                break;
            case "Admin Functions":
                cardLayout.show(cardPanel, "Password");
                break;
            case "Book Passenger Ticket":
                outputArea.append("Enter Train Number to book passenger ticket: ");
                currentCommand = "BookPassengerTicket";
                inputField.requestFocus();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    private static void handleAdminChoice(String choice) {
        outputArea.append("\n");
        switch (choice) {
            case "Generate Detailed Report":
                generateDetailedReport();
                break;
            case "Modify Train Data":
                outputArea.append("Enter Train Number to modify: ");
                currentCommand = "ModifyTrain";
                inputField.requestFocus();
                break;
            case "Delete Passenger Data":
                outputArea.append("Enter Passenger Serial Number to delete: ");
                currentCommand = "DeletePassenger";
                inputField.requestFocus();
                break;
            case "Reset Ticket Counters":
                resetTicketCounters();
                break;
            case "Add New Train":
                addNewTrainDialog();
                break;
            case "View All Trains":
                viewAllTrains();
                break;
            case "View All Passengers":
                viewAllPassengers();
                break;
            case "View All Platform Tickets":
                viewAllPlatformTickets();
                break;
            case "Back to Main Menu":
                cardLayout.show(cardPanel, "MainMenu");
                break;
        }
    }

    private static void processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            outputArea.append("❌ Please enter a valid input!\n");
            return;
        }

        String command = currentCommand;
        inputField.setText("");
        
        if (command == null) {
            outputArea.append("❌ No command selected. Please choose an option from the menu.\n");
            return;
        }
        
        try {
            switch (command) {
                case "SearchByTrain":
                    searchByTrainNumber(input.trim());
                    break;
                case "SearchByDestination":
                    searchByDestination(input.trim());
                    break;
                case "CheckSeatAvailability":
                    checkSeatAvailability(input.trim());
                    break;
                case "CheckPlatformAvailability":
                    checkPlatformTicketAvailability(input.trim());
                    break;
                case "BookPlatformTicket":
                    bookPlatformTicket(input.trim());
                    break;
                case "QueryPlatformTickets":
                    queryPlatformTickets(input.trim());
                    break;
                case "ModifyTrain":
                    modifyTrainData(input.trim());
                    break;
                case "DeletePassenger":
                    deletePassengerData(input.trim());
                    break;
                case "BookPassengerTicket":
                    bookPassengerTicket(input.trim());
                    break;
            }
        } catch (Exception e) {
            outputArea.append("❌ Error processing input: " + e.getMessage() + "\n");
        }
    }

    private static boolean authenticate(String password) {
        return password.equals("sou123") || password.equals("deep12") || password.equals("prak12");
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int)cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private static String getTrainType(String trainNumber) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(1)).equals(trainNumber)) {
                    return getCellValue(row.getCell(7));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static int calculateTicketPrice(String trainNumber) {
        String trainType = getTrainType(trainNumber);
        switch (trainType) {
            case "Superfast Express":
                return random.nextInt(SUPERFAST_MAX - SUPERFAST_MIN + 1) + SUPERFAST_MIN;
            case "Mail":
                return random.nextInt(MAIL_MAX - MAIL_MIN + 1) + MAIL_MIN;
            case "Local":
                return random.nextInt(LOCAL_MAX - LOCAL_MIN + 1) + LOCAL_MIN;
            default:
                return 0;
        }
    }

    private static int getAvailableSeats(String trainNumber) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(1)).equals(trainNumber)) {
                    return Integer.parseInt(getCellValue(row.getCell(15)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void updateSeatCount(String trainNumber, int seatsBooked) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(1)).equals(trainNumber)) {
                    int currentSeats = Integer.parseInt(getCellValue(row.getCell(15)));
                    row.getCell(15).setCellValue(currentSeats - seatsBooked);
                    
                    FileOutputStream fos = new FileOutputStream(TRAIN_DATA_FILE);
                    workbook.write(fos);
                    fos.close();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void savePassengerData(String trainNumber, String name, int age, 
                                        String gender, String status) {
        try (FileInputStream fis = new FileInputStream(PASSENGER_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            
            row.createCell(0).setCellValue(serialNumberPassenger++);
            row.createCell(1).setCellValue(name);
            row.createCell(2).setCellValue(trainNumber);
            row.createCell(3).setCellValue(calculateTicketPrice(trainNumber));
            row.createCell(4).setCellValue(age);
            row.createCell(5).setCellValue(gender);
            row.createCell(6).setCellValue(status);
            
            FileOutputStream fos = new FileOutputStream(PASSENGER_TICKETS_FILE);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void savePlatformTicketData(String trainNumber, int quantity, int pricePerTicket) {
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 0; i < quantity; i++) {
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0).setCellValue(serialNumberPlatform++);
                row.createCell(1).setCellValue(trainNumber);
                row.createCell(2).setCellValue(pricePerTicket);
            }
            
            FileOutputStream fos = new FileOutputStream(PLATFORM_TICKETS_FILE);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateDetailedReport() {
        outputArea.append("\n=== Detailed Report ===\n");
        outputArea.append("Total Passenger Tickets Sold: " + totalTicketsSold + "\n");
        outputArea.append("Total Platform Tickets Sold: " + platformTickets.values().stream().mapToInt(Integer::intValue).sum() + "\n");
        outputArea.append("Platform Tickets Sold per Train:\n");
        if (platformTickets.isEmpty()) {
            outputArea.append("No platform tickets sold yet.\n");
        } else {
            platformTickets.forEach((trainNumber, count) -> 
                outputArea.append("Train " + trainNumber + ": " + count + " tickets\n"));
        }
    }

    private static void modifyTrainData(String trainNumber) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentTrainNumber = getCellValue(row.getCell(1));
                if (currentTrainNumber.equals(trainNumber)) {
                    found = true;
                    outputArea.append("\nCurrent Train Details:\n");
                    outputArea.append("Train Number: " + currentTrainNumber + "\n");
                    outputArea.append("From: " + getCellValue(row.getCell(2)) + "\n");
                    outputArea.append("To: " + getCellValue(row.getCell(3)) + "\n");
                    outputArea.append("Seats: " + getCellValue(row.getCell(15)) + "\n");

                    String newSeats = JOptionPane.showInputDialog(mainFrame, "Enter new number of seats:");
                    if (newSeats != null && !newSeats.isEmpty()) {
                        try {
                            int seats = Integer.parseInt(newSeats);
                            if (seats < 0) {
                                outputArea.append("❌ Number of seats cannot be negative!\n");
                                return;
                            }
                            row.getCell(15).setCellValue(seats);

                            FileOutputStream fos = new FileOutputStream(TRAIN_DATA_FILE);
                            workbook.write(fos);
                            fos.close();
                            outputArea.append("✅ Train data modified successfully!\n");
                        } catch (NumberFormatException e) {
                            outputArea.append("❌ Invalid number entered! Please enter a valid positive integer.\n");
                        }
                    }
                    break;
                }
            }
            if (!found) outputArea.append("❌ Train not found!\n");
        } catch (Exception e) {
            outputArea.append("❌ Error modifying train data: " + e.getMessage() + "\n");
        }
    }

    private static void deletePassengerData(String serialNumber) {
        try {
            if (!serialNumber.matches("\\d+")) {
                outputArea.append("❌ Invalid serial number! Please enter a number.\n");
                return;
            }

            File file = new File(PASSENGER_TICKETS_FILE);
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentSerialNumber = getCellValue(row.getCell(0));
                if (currentSerialNumber.equals(serialNumber)) {
                    found = true;
                    sheet.removeRow(row);
                    outputArea.append("✅ Passenger data deleted successfully!\n");
                    break;
                }
            }

            if (!found) outputArea.append("❌ Passenger not found!\n");

            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();
        } catch (Exception e) {
            outputArea.append("❌ Error deleting passenger data: " + e.getMessage() + "\n");
        }
    }

    private static void resetTicketCounters() {
        totalTicketsSold = 0;
        platformTickets.clear();
        serialNumberPassenger = 1;
        serialNumberPlatform = 1;
        outputArea.append("✅ Ticket counters reset successfully!\n");
    }

    private static void addNewTrainDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        JTextField trainNumberField = new JTextField();
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();
        JTextField departureField = new JTextField();
        JTextField arrivalField = new JTextField();
        JComboBox<String> typeField = new JComboBox<>(new String[]{"Superfast Express", "Mail", "Local"});
        JTextField seatsField = new JTextField();
        
        panel.add(new JLabel("Train Number:"));
        panel.add(trainNumberField);
        panel.add(new JLabel("From Station:"));
        panel.add(fromField);
        panel.add(new JLabel("To Station:"));
        panel.add(toField);
        panel.add(new JLabel("Departure Time (HH:MM):"));
        panel.add(departureField);
        panel.add(new JLabel("Arrival Time (HH:MM):"));
        panel.add(arrivalField);
        panel.add(new JLabel("Train Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Number of Seats:"));
        panel.add(seatsField);
        
        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Add New Train", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String trainNumber = trainNumberField.getText().trim();
                if (trainNumber.isEmpty()) {
                    outputArea.append("❌ Train number cannot be empty!\n");
                    return;
                }
                
                String from = fromField.getText().trim();
                if (from.isEmpty()) {
                    outputArea.append("❌ From station cannot be empty!\n");
                    return;
                }
                
                String to = toField.getText().trim();
                if (to.isEmpty()) {
                    outputArea.append("❌ To station cannot be empty!\n");
                    return;
                }
                
                String departureTime = departureField.getText().trim();
                String arrivalTime = arrivalField.getText().trim();
                String trainType = (String) typeField.getSelectedItem();
                
                int seats = Integer.parseInt(seatsField.getText().trim());
                if (seats <= 0) {
                    outputArea.append("❌ Number of seats must be positive!\n");
                    return;
                }
                
                addNewTrain(trainNumber, from, to, departureTime, arrivalTime, trainType, seats);
            } catch (NumberFormatException e) {
                outputArea.append("❌ Invalid number of seats entered! Please enter a valid positive integer.\n");
            }
        }
    }

    private static void addNewTrain(String trainNumber, String from, String to, 
            String departureTime, String arrivalTime, String trainType, int seats) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Check if train already exists
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentTrainNumber = getCellValue(row.getCell(1));
                if (currentTrainNumber.equals(trainNumber)) {
                    outputArea.append("❌ Train with this number already exists!\n");
                    return;
                }
            }

            Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            newRow.createCell(0).setCellValue(sheet.getLastRowNum());
            newRow.createCell(1).setCellValue(trainNumber);
            newRow.createCell(2).setCellValue(from);
            newRow.createCell(3).setCellValue(to);
            newRow.createCell(4).setCellValue(trainNumber);
            newRow.createCell(5).setCellValue(arrivalTime);
            newRow.createCell(6).setCellValue(departureTime);
            newRow.createCell(7).setCellValue(trainType);
            newRow.createCell(8).setCellValue("Yes");
            newRow.createCell(9).setCellValue("No");
            newRow.createCell(10).setCellValue("No");
            newRow.createCell(11).setCellValue("Yes");
            newRow.createCell(12).setCellValue("No");
            newRow.createCell(13).setCellValue("Yes");
            newRow.createCell(14).setCellValue("No");
            newRow.createCell(15).setCellValue(seats);

            FileOutputStream fos = new FileOutputStream(TRAIN_DATA_FILE);
            workbook.write(fos);
            fos.close();
            outputArea.append("✅ New train added successfully!\n");
        } catch (Exception e) {
            outputArea.append("❌ Error adding new train: " + e.getMessage() + "\n");
        }
    }

    private static void viewAllTrains() {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            outputArea.append("\n=== All Trains ===\n");

            if (sheet.getPhysicalNumberOfRows() <= 1) {
                outputArea.append("No trains found in the database.\n");
                return;
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                outputArea.append(String.format("%-8s %-15s %-15s %-6s %-8s %-8s %-20s %-4s\n",
                    getCellValue(row.getCell(1)),
                    getCellValue(row.getCell(2)),
                    getCellValue(row.getCell(3)),
                    getCellValue(row.getCell(5)),
                    getCellValue(row.getCell(6)),
                    getCellValue(row.getCell(7)),
                    getCellValue(row.getCell(8)),
                    getCellValue(row.getCell(15))));
            }
        } catch (Exception e) {
            outputArea.append("❌ Error viewing all trains: " + e.getMessage() + "\n");
        }
    }

    private static void viewAllPassengers() {
        try (FileInputStream fis = new FileInputStream(PASSENGER_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            outputArea.append("\n=== All Passengers ===\n");

            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                outputArea.append("No passenger records found.\n");
                return;
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                outputArea.append(String.format("%-6s %-20s %-10s %-6s %-3s %-6s %-10s\n",
                    getCellValue(row.getCell(0)),
                    getCellValue(row.getCell(1)),
                    getCellValue(row.getCell(2)),
                    getCellValue(row.getCell(3)),
                    getCellValue(row.getCell(4)),
                    getCellValue(row.getCell(5)),
                    getCellValue(row.getCell(6))));
            }
        } catch (Exception e) {
            outputArea.append("❌ Error viewing all passengers: " + e.getMessage() + "\n");
        }
    }

    private static void viewAllPlatformTickets() {
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            outputArea.append("\n=== All Platform Tickets ===\n");

            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                outputArea.append("No platform tickets sold yet.\n");
                return;
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                outputArea.append(String.format("%-6s %-10s %-6s\n",
                    getCellValue(row.getCell(0)),
                    getCellValue(row.getCell(1)),
                    getCellValue(row.getCell(2))));
            }
        } catch (Exception e) {
            outputArea.append("❌ Error viewing all platform tickets: " + e.getMessage() + "\n");
        }
    }

    private static void searchByTrainNumber(String trainNumber) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentTrainNumber = getCellValue(row.getCell(1));
                if (currentTrainNumber.equals(trainNumber)) {
                    found = true;
                    outputArea.append("\n=== Train Details ===\n");
                    outputArea.append("Train Number: " + currentTrainNumber + "\n");
                    outputArea.append("From: " + getCellValue(row.getCell(2)) + "\n");
                    outputArea.append("To: " + getCellValue(row.getCell(3)) + "\n");
                    outputArea.append("Arrival Time: " + getCellValue(row.getCell(5)) + "\n");
                    outputArea.append("Departure Time: " + getCellValue(row.getCell(6)) + "\n");
                    outputArea.append("Train Type: " + getCellValue(row.getCell(7)) + "\n");
                    outputArea.append("Available Seats: " + getCellValue(row.getCell(15)) + "\n");

                    int option = JOptionPane.showConfirmDialog(mainFrame, 
                        "Do you want to book a ticket for this train?", 
                        "Book Ticket", JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        bookPassengerTicket(trainNumber);
                    }
                    break;
                }
            }
            if (!found) outputArea.append("❌ Train not found!\n");
        } catch (Exception e) {
            outputArea.append("❌ Error searching by train number: " + e.getMessage() + "\n");
        }
    }

    private static void searchByDestination(String destination) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            outputArea.append("\n=== Trains to " + destination + " ===\n");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentDestination = getCellValue(row.getCell(3));
                if (currentDestination.equalsIgnoreCase(destination)) {
                    found = true;
                    outputArea.append(String.format("%-8s %-15s %-15s %-8s\n",
                        getCellValue(row.getCell(1)),
                        getCellValue(row.getCell(2)),
                        currentDestination,
                        getCellValue(row.getCell(6))));
                }
            }
            if (!found) outputArea.append("❌ No trains found for the destination: " + destination + "\n");
        } catch (Exception e) {
            outputArea.append("❌ Error searching by destination: " + e.getMessage() + "\n");
        }
    }

    private static void checkSeatAvailability(String trainNumber) {
        try (FileInputStream fis = new FileInputStream(TRAIN_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentTrainNumber = getCellValue(row.getCell(1));
                if (currentTrainNumber.equals(trainNumber)) {
                    found = true;
                    int seats = Integer.parseInt(getCellValue(row.getCell(15)));
                    outputArea.append("\nAvailable Seats for Train " + trainNumber + ": " + seats + "\n");
                    break;
                }
            }
            if (!found) outputArea.append("❌ Train not found!\n");
        } catch (Exception e) {
            outputArea.append("❌ Error checking seat availability: " + e.getMessage() + "\n");
        }
    }

    private static void checkPlatformTicketAvailability(String trainNumber) {
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            int count = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentTrainNumber = getCellValue(row.getCell(1));
                if (currentTrainNumber.equals(trainNumber)) {
                    count++;
                }
            }

            outputArea.append("\nPlatform tickets available for train " + trainNumber + ": " + count + "\n");
        } catch (Exception e) {
            outputArea.append("❌ Error checking platform ticket availability: " + e.getMessage() + "\n");
        }
    }

    private static void generateHourlyReport() {
        outputArea.append("\n=== Hourly Report ===\n");
        outputArea.append("Total Passenger Tickets Sold: " + totalTicketsSold + "\n");
        outputArea.append("Total Platform Tickets Sold: " + 
            platformTickets.values().stream().mapToInt(Integer::intValue).sum() + "\n");
    }

    private static void queryPlatformTickets(String trainNumber) {
        try (FileInputStream fis = new FileInputStream(PLATFORM_TICKETS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            outputArea.append("\n=== Platform Tickets for Train " + trainNumber + " ===\n");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String currentTrainNumber = getCellValue(row.getCell(1));
                if (currentTrainNumber.equals(trainNumber)) {
                    found = true;
                    outputArea.append("Serial No: " + getCellValue(row.getCell(0)) + 
                        ", Price: ₹" + getCellValue(row.getCell(2)) + "\n");
                }
            }
            if (!found) outputArea.append("No platform tickets found for this train.\n");
        } catch (Exception e) {
            outputArea.append("❌ Error querying platform tickets: " + e.getMessage() + "\n");
        }
    }

    private static void bookPassengerTicket(String trainNumber) {
        try {
            // First check if train exists
            String trainType = getTrainType(trainNumber);
            if (trainType.isEmpty()) {
                outputArea.append("❌ Train not found!\n");
                return;
            }

            // Calculate ticket price
            int pricePerTicket = calculateTicketPrice(trainNumber);
            outputArea.append("\nTicket Price for " + trainType + ": ₹" + pricePerTicket + "\n");

            // Get number of passengers
            String passengerCountStr = JOptionPane.showInputDialog(mainFrame, 
                "Enter number of passengers:");
            if (passengerCountStr == null || passengerCountStr.isEmpty()) {
                outputArea.append("❌ Booking cancelled.\n");
                return;
            }
            
            int passengerCount;
            try {
                passengerCount = Integer.parseInt(passengerCountStr);
                if (passengerCount <= 0) {
                    outputArea.append("❌ Invalid number of passengers! Must be at least 1.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                outputArea.append("❌ Please enter a valid number!\n");
                return;
            }

            // Check seat availability
            int availableSeats = getAvailableSeats(trainNumber);
            if (availableSeats < passengerCount) {
                outputArea.append("❌ Not enough seats available! Only " + 
                    availableSeats + " seats left.\n");
                return;
            }

            // Create passenger input dialog
            JPanel passengerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            List<JTextField> nameFields = new ArrayList<>();
            List<JTextField> ageFields = new ArrayList<>();
            List<JComboBox<String>> genderFields = new ArrayList<>();

            for (int i = 0; i < passengerCount; i++) {
                passengerPanel.add(new JLabel("Passenger " + (i + 1) + " Name:"));
                JTextField nameField = new JTextField();
                nameFields.add(nameField);
                passengerPanel.add(nameField);

                                passengerPanel.add(new JLabel("Age:"));
                JTextField ageField = new JTextField();
                ageFields.add(ageField);
                passengerPanel.add(ageField);

                passengerPanel.add(new JLabel("Gender:"));
                JComboBox<String> genderBox = new JComboBox<>(
                    new String[]{"Male", "Female", "Other"});
                genderFields.add(genderBox);
                passengerPanel.add(genderBox);
            }

            int result = JOptionPane.showConfirmDialog(mainFrame, passengerPanel, 
                "Enter Passenger Details", JOptionPane.OK_CANCEL_OPTION);
            
            if (result != JOptionPane.OK_OPTION) {
                outputArea.append("❌ Booking cancelled.\n");
                return;
            }

            // Validate and collect passenger details
            List<Passenger> passengers = new ArrayList<>();
            for (int i = 0; i < passengerCount; i++) {
                String name = nameFields.get(i).getText().trim();
                if (name.isEmpty()) {
                    outputArea.append("❌ Name cannot be empty for passenger " + (i + 1) + "!\n");
                    return;
                }
                
                int age;
                try {
                    age = Integer.parseInt(ageFields.get(i).getText());
                    if (age <= 0 || age > 120) {
                        outputArea.append("⚠️ Invalid age for passenger " + (i + 1) + 
                            "! Using default age 25.\n");
                        age = 25;
                    }
                } catch (NumberFormatException e) {
                    outputArea.append("⚠️ Invalid age for passenger " + (i + 1) + 
                        "! Using default age 25.\n");
                    age = 25;
                }
                
                String gender = ((String)genderFields.get(i).getSelectedItem()).substring(0, 1);
                passengers.add(new Passenger(name, age, gender));
            }

            // Calculate total amount
            int totalAmount = pricePerTicket * passengerCount;
            outputArea.append("\nTotal Amount: ₹" + totalAmount + "\n");

            // Process payment
            String amountPaidStr = JOptionPane.showInputDialog(mainFrame, 
                "Enter amount to pay (Total: ₹" + totalAmount + "):");
            if (amountPaidStr == null || amountPaidStr.isEmpty()) {
                outputArea.append("❌ Payment cancelled.\n");
                return;
            }
            
            int amountPaid;
            try {
                amountPaid = Integer.parseInt(amountPaidStr);
                if (amountPaid <= 0) {
                    outputArea.append("❌ Invalid amount entered!\n");
                    return;
                }
            } catch (NumberFormatException e) {
                outputArea.append("❌ Invalid amount entered!\n");
                return;
            }

            if (amountPaid < totalAmount) {
                outputArea.append("❌ Insufficient amount! Payment failed.\n");
                return;
            }

            // Save passenger data
            for (Passenger passenger : passengers) {
                savePassengerData(trainNumber, passenger.name, passenger.age, 
                    passenger.gender, "Confirmed");
            }

            // Update seat count
            updateSeatCount(trainNumber, passengerCount);

            // Handle change if any
            if (amountPaid > totalAmount) {
                outputArea.append("\n✅ Payment successful! Please take your change: ₹" + 
                    (amountPaid - totalAmount) + "\n");
            } else {
                outputArea.append("\n✅ Payment successful!\n");
            }

            totalTicketsSold += passengerCount;
            outputArea.append("✅ Tickets booked successfully for " + 
                passengerCount + " passenger(s)!\n");

        } catch (Exception e) {
            outputArea.append("❌ Error booking tickets: " + e.getMessage() + "\n");
        }
    }

    private static void bookPlatformTicket(String trainNumber) {
        try {
            // Check if train exists
            if (getTrainType(trainNumber).isEmpty()) {
                outputArea.append("❌ Train not found!\n");
                return;
            }

            // Get number of platform tickets
            String quantityStr = JOptionPane.showInputDialog(mainFrame, 
                "Enter number of platform tickets:");
            if (quantityStr == null || quantityStr.isEmpty()) {
                outputArea.append("❌ Booking cancelled.\n");
                return;
            }
            
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    outputArea.append("❌ Invalid quantity! Must be at least 1.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                outputArea.append("❌ Please enter a valid number!\n");
                return;
            }

            // Calculate total amount
            int pricePerTicket = random.nextInt(PLATFORM_TICKET_MAX - PLATFORM_TICKET_MIN + 1) + PLATFORM_TICKET_MIN;
            int totalAmount = pricePerTicket * quantity;
            outputArea.append("\nTotal Amount: ₹" + totalAmount + "\n");

            // Process payment
            String amountPaidStr = JOptionPane.showInputDialog(mainFrame, 
                "Enter amount to pay (Total: ₹" + totalAmount + "):");
            if (amountPaidStr == null || amountPaidStr.isEmpty()) {
                outputArea.append("❌ Payment cancelled.\n");
                return;
            }
            
            int amountPaid;
            try {
                amountPaid = Integer.parseInt(amountPaidStr);
                if (amountPaid <= 0) {
                    outputArea.append("❌ Invalid amount entered!\n");
                    return;
                }
            } catch (NumberFormatException e) {
                outputArea.append("❌ Invalid amount entered!\n");
                return;
            }

            if (amountPaid < totalAmount) {
                outputArea.append("❌ Insufficient amount! Payment failed.\n");
                return;
            }

            // Save platform ticket data
            savePlatformTicketData(trainNumber, quantity, pricePerTicket);

            // Handle change if any
            if (amountPaid > totalAmount) {
                outputArea.append("\n✅ Payment successful! Please take your change: ₹" + 
                    (amountPaid - totalAmount) + "\n");
            } else {
                outputArea.append("\n✅ Payment successful!\n");
            }

            // Update platform ticket count
            platformTickets.put(trainNumber, platformTickets.getOrDefault(trainNumber, 0) + quantity);
            
            outputArea.append("✅ " + quantity + " platform ticket(s) booked successfully!\n");
            outputArea.append("Price per ticket: ₹" + pricePerTicket + "\n");

        } catch (Exception e) {
            outputArea.append("❌ Error booking platform tickets: " + e.getMessage() + "\n");
        }
    }
}
