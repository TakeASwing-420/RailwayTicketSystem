package com.example.model;

public class PlatformTicket {
    private int serialNumber;
    private String trainNumber;
    private int price;

    public PlatformTicket(String trainNumber, int price) {
        this.trainNumber = trainNumber;
        this.price = price;
    }

    // Getters
    public int getSerialNumber() { return serialNumber; }
    public String getTrainNumber() { return trainNumber; }
    public int getPrice() { return price; }
    
    // Setter for serial number
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }
}
