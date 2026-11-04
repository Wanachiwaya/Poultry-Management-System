package zm.co.poultryfarm.model;

import java.time.LocalDate;

public class Expense implements FarmRecord {

    private String id;
    private String description;
    private double amount;
    private LocalDate date;

    // Default constructor
    public Expense() {
    }

    // Constructor with all fields
    public Expense(String id, String description,
            double amount, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    // Getters and setters
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Returns the expense description
    @Override
    public String getSummary() {
        return description;
    }
}
