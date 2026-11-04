package zm.co.poultryfarm.model;

import java.time.LocalDate;

public class EggProduction implements FarmRecord {

    private String id;
    private String flockId;
    private int eggsCollected;
    private LocalDate date;

    // Default constructor
    public EggProduction() {
    }

    // Constructor with all fields
    public EggProduction(String id, String flockId,
            int eggsCollected, LocalDate date) {
        this.id = id;
        this.flockId = flockId;
        this.eggsCollected = eggsCollected;
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

    public String getFlockId() {
        return flockId;
    }

    public void setFlockId(String flockId) {
        this.flockId = flockId;
    }

    public int getEggsCollected() {
        return eggsCollected;
    }

    public void setEggsCollected(int eggsCollected) {
        this.eggsCollected = eggsCollected;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Returns a description of the production record
    @Override
    public String getSummary() {
        return eggsCollected + " eggs collected";
    }

    // Returns the number of eggs collected
    @Override
    public double getAmount() {
        return eggsCollected;
    }
}
