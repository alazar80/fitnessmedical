package com.example.sql;

public class Feedback {
    public int id, user_id, doctor_id, rating;
    public String subject, message, created_at, status, response;

    public Feedback(int id, int user_id, int doctor_id, String subject, String message,
                    int rating, String created_at, String status, String response) {
        this.id = id;
        this.user_id = user_id;
        this.doctor_id = doctor_id;
        this.subject = subject;
        this.message = message;
        this.rating = rating;
        this.created_at = created_at;
        this.status = status;
        this.response = response;
    }
}

