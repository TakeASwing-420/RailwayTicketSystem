package com.example.model;

public class Passenger {
    private int serialNumber;
    private String name;
    private int age;
    private String gender;
    private String status;
    private String trainNumber;
    private int ticketPrice;

    public Passenger(String name, int age, String gender, String status, String trainNumber, int ticketPrice) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.status = status;
        this.trainNumber = trainNumber;
        this.ticketPrice = ticketPrice;
    }

    // Getters and Setters
    public int getSerialNumber() { return serialNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getStatus() { return status; }
    public String getTrainNumber() { return trainNumber; }
    public int getTicketPrice() { return ticketPrice; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }
}
