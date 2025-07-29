package com.example.sql;

public class Doctor {
    private String name;
    private String phoneNumber;
    private int userId;
    private int id;
    public Doctor(String name, String phoneNumber, int userId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.id=id;
    }


    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}


    public int getId() {return id;}
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getUserId() { return userId; }
}
