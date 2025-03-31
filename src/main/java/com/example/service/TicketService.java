package com.example.service;

import com.example.dao.PassengerDao;
import com.example.dao.PlatformTicketDao;
import com.example.dao.TrainDao;
import com.example.model.Train;
import java.io.IOException;
import java.util.*;
import java.util.Random;
import com.example.model.Passenger;
import com.example.model.PlatformTicket;

public class TicketService {
    private final TrainDao trainDao;
    private final PassengerDao passengerDao;
    private final PlatformTicketDao platformTicketDao;
    private final Random random;
    
    private static final int PLATFORM_TICKET_MIN = 10, PLATFORM_TICKET_MAX = 50;
    private static final int SUPERFAST_MIN = 1500, SUPERFAST_MAX = 2500;
    private static final int MAIL_MIN = 500, MAIL_MAX = 1500;
    private static final int LOCAL_MIN = 50, LOCAL_MAX = 500;
    
    private int totalTicketsSold = 0;
    private final Map<String, Integer> platformTickets = new HashMap<>();

    public TicketService() {
        this.trainDao = new TrainDao();
        this.passengerDao = new PassengerDao();
        this.platformTicketDao = new PlatformTicketDao();
        this.random = new Random();
    }

    public void deletePassenger(int serialNumber) throws IOException {
        passengerDao.deletePassenger(serialNumber);
    }

    public void resetCounters() {
        totalTicketsSold = 0;
        platformTickets.clear();
        passengerDao.resetSerialNumber();
        platformTicketDao.resetSerialNumber();
    }

    public void updateTrainSeats(String trainNumber, int seats) throws IOException {
        trainDao.updateSeats(trainNumber, seats);
    }

    public void addNewTrain(String trainNumber, String from, String to, 
                          String departure, String arrival, String type, int seats) throws IOException {
        if (trainDao.getTrainByNumber(trainNumber) != null) {
            throw new IOException("Train with this number already exists");
        }
        trainDao.addNewTrain(trainNumber, from, to, departure, arrival, type, seats);
    }

    public List<String> getAllPassengers() throws IOException {
        List<String> passengers = new ArrayList<>();
        passengers.add(String.format("%-6s %-20s %-10s %-8s %-5s %-6s %-10s",
            "No.", "Name", "Train No", "Price", "Age", "Gender", "Status"));
        passengers.add("---------------------------------------------------------------");
        
        for (Passenger passenger : passengerDao.getAllPassengers()) {
            passengers.add(String.format("%-6s %-20s %-10s %-8s %-5s %-6s %-10s",
                passenger.getSerialNumber(),
                passenger.getName(),
                passenger.getTrainNumber(),
                "₹" + passenger.getTicketPrice(),
                passenger.getAge(),
                passenger.getGender(),
                passenger.getStatus()));
        }
        return passengers;
    }

    public List<String> getAllPlatformTickets() throws IOException {
        List<String> tickets = new ArrayList<>();
        tickets.add(String.format("%-6s %-10s %-8s", "No.", "Train No", "Price"));
        tickets.add("---------------------------");
        
        for (PlatformTicket ticket : platformTicketDao.getAllTickets()) {
            tickets.add(String.format("%-6s %-10s %-8s",
                ticket.getSerialNumber(),
                ticket.getTrainNumber(),
                "₹" + ticket.getPrice()));
        }
        return tickets;
    }

    public int getPlatformTicketCount(String trainNumber) throws IOException {
        return platformTicketDao.getTicketCountForTrain(trainNumber);
    }

    public List<String> getPlatformTickets(String trainNumber) throws IOException {
        List<String> tickets = new ArrayList<>();
        tickets.add("Serial No   Price");
        tickets.add("----------------");
        
        for (Map<String, String> ticket : platformTicketDao.getTicketsByTrain(trainNumber)) {
            tickets.add(String.format("%-10s ₹%-8s",
                ticket.get("serial"),
                ticket.get("price")));
        }
        return tickets;
    }

    public void bookPassengerTicket(String trainNumber, String name, int age, 
                                  String gender, String status, int price) throws IOException {
        passengerDao.savePassenger(new Passenger(name, age, gender, status, trainNumber, price));
        Train train = trainDao.getTrainByNumber(trainNumber);
        if (train != null) {
            trainDao.updateSeats(trainNumber, train.getAvailableSeats() - 1);
        }
        totalTicketsSold++;
    }

    public void bookPlatformTicket(String trainNumber, int price) throws IOException {
        platformTicketDao.saveTicket(trainNumber, price);
        platformTickets.put(trainNumber, platformTickets.getOrDefault(trainNumber, 0) + 1);
    }

    public int calculatePassengerTicketPrice(String trainNumber) throws IOException {
        Train train = trainDao.getTrainByNumber(trainNumber);
        if (train == null) return 0;
        
        switch (train.getType()) {
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

    public int calculatePlatformTicketPrice() {
        return random.nextInt(PLATFORM_TICKET_MAX - PLATFORM_TICKET_MIN + 1) + PLATFORM_TICKET_MIN;
    }

    public List<Train> searchByDestination(String destination) throws IOException {
        List<Train> result = new ArrayList<>();
        for (Train train : trainDao.getAllTrains()) {
            if (train.getTo().equalsIgnoreCase(destination)) {
                result.add(train);
            }
        }
        return result;
    }

    public String generateHourlyReport() {
        return "=== Hourly Report ===\n" +
               "Total Passenger Tickets Sold: " + totalTicketsSold + "\n" +
               "Total Platform Tickets Sold: " + 
               platformTickets.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String generateDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n=== Detailed Report ===\n");
        report.append("Total Passenger Tickets Sold: ").append(totalTicketsSold).append("\n");
        report.append("Total Platform Tickets Sold: ").append(
            platformTickets.values().stream().mapToInt(Integer::intValue).sum()).append("\n\n");
        report.append("Platform Tickets Sold per Train:\n");
        report.append("Train No   Tickets\n");
        report.append("-----------------\n");
        
        if (platformTickets.isEmpty()) {
            report.append("No platform tickets sold yet.\n");
        } else {
            platformTickets.forEach((trainNumber, count) -> 
                report.append(String.format("%-10s %-8s\n", trainNumber, count)));
        }
        return report.toString();
    }

    public Train getTrainByNumber(String trainNumber) throws IOException {
        return trainDao.getTrainByNumber(trainNumber);
    }

    public List<Train> getAllTrains() throws IOException {
        return trainDao.getAllTrains();
    }
}
