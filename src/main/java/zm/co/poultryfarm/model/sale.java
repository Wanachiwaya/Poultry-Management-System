package zm.co.poultryfarm.model;

import java.time.LocalDate;

public class Sale implements FarmRecord {

    private String id;
    private String itemType;
    private int quantity;
    private double unitPrice;
    private LocalDate date;

    // Default constructor
    public Sale() {
    }

    // Constructor with all fields
    public Sale(String id, String itemType, int quantity,
            double unitPrice, LocalDate date) {
        this.id = id;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Returns a description of the sale
    @Override
    public String getSummary() {
        return quantity + " " + itemType + " sold";
    }

    // Calculates the total sale value
    @Override
    public double getAmount() {
        return quantity * unitPrice;
    }
}
