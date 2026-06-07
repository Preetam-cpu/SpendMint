package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    private String type;

    private String category;

    private String date;

    /* =========================
       USER RELATION
    ========================= */

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "user_id")

    @JsonIgnore
    private User user;

    /* =========================
       CONSTRUCTORS
    ========================= */

    public Transaction() {

    }

    public Transaction(
            double amount,
            String type,
            String category,
            String date
    ) {

        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    /* =========================
       GETTERS & SETTERS
    ========================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}