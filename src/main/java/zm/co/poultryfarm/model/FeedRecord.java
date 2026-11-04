package zm.co.poultryfarm.model;

import java.time.LocalDate;

public class FeedRecord implements FarmRecord {

    private String id;
    private String flockId;
    private String feedType;
    private int bagsPurchased;
    private int bagsUsed;
    private LocalDate date;

    // Default constructor
    public FeedRecord() {
    }

    // Constructor with all fields
    public FeedRecord(String id, String flockId, String feedType,
            int bagsPurchased, int bagsUsed, LocalDate date) {
        this.id = id;
        this.flockId = flockId;
        this.feedType = feedType;
        this.bagsPurchased = bagsPurchased;
        this.bagsUsed = bagsUsed;
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

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public int getBagsPurchased() {
        return bagsPurchased;
    }

    public void setBagsPurchased(int bagsPurchased) {
        this.bagsPurchased = bagsPurchased;
    }

    public int getBagsUsed() {
        return bagsUsed;
    }

    public void setBagsUsed(int bagsUsed) {
        this.bagsUsed = bagsUsed;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Returns a summary of feed usage
    @Override
    public String getSummary() {
        return feedType + " feed: " + bagsPurchased + " bags purchased, "
                + bagsUsed + " bags used";
    }

    // Returns the number of bags used
    @Override
    public double getAmount() {
        return bagsUsed;
    }
}
