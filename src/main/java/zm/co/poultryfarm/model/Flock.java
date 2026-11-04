package zm.co.poultryfarm.model;

import java.time.LocalDate;

public class Flock {

    private String id;
    private String type;
    private int quantity;
    private LocalDate acquisitionDate;

    // Default constructor
    public Flock() {
    }

    // Constructor with all fields
    public Flock(String id, String type, int quantity,
            LocalDate acquisitionDate) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.acquisitionDate = acquisitionDate;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }
}
