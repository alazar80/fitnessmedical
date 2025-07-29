package com.example.sql;

public class User {
    // 1) instance fields, not static
    private int    id;
    private String username;
    private String email;
    private String phone;
    private String fitnessGoal;
    private String experienceLevel;

    // 2) empty ctor for frameworks / JSON libs
    public User() { }

    // 3) your old 3-arg ctor delegates into the new 4-arg
    public User(int id, String email, String phone) {
        this(id, /*username*/"", email, phone);
    }

    // 4) new 4-arg ctor
    public User(int id, String username, String email, String phone) {
        this.id                = id;
        this.username          = username;
        this.email             = email;
        this.phone             = phone;
        this.fitnessGoal       = "";
        this.experienceLevel   = "";
    }

    // getters & setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public String getFitnessGoal()     { return fitnessGoal; }
    public String getExperienceLevel() { return experienceLevel; }

    public void setId(int id)                     { this.id = id; }
    public void setUsername(String username)      { this.username = username; }
    public void setEmail(String email)            { this.email = email; }
    public void setPhone(String phone)            { this.phone = phone; }
    public void setFitnessGoal(String goal)       { this.fitnessGoal = goal; }
    public void setExperienceLevel(String level)  { this.experienceLevel = level; }
}
