package com.example.model;

public class Train {
    private String number;
    private String from;
    private String to;
    private String departure;
    private String arrival;
    private String type;
    private int availableSeats;

    public Train(String number, String from, String to, String departure, 
                String arrival, String type, int availableSeats) {
        this.number = number;
        this.from = from;
        this.to = to;
        this.departure = departure;
        this.arrival = arrival;
        this.type = type;
        this.availableSeats = availableSeats;
    }

    // Getters
    public String getNumber() { return number; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public String getType() { return type; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int seats) { this.availableSeats = seats; }
}
