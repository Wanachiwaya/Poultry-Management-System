package zm.co.poultryfarm.manager;

import java.util.ArrayList;
import java.util.List;

import zm.co.poultryfarm.exception.InvalidRecordException;
import zm.co.poultryfarm.exception.RecordNotFoundException;
import zm.co.poultryfarm.model.EggProduction;
import zm.co.poultryfarm.model.Expense;
import zm.co.poultryfarm.model.FarmRecord;
import zm.co.poultryfarm.model.FeedRecord;
import zm.co.poultryfarm.model.Flock;
import zm.co.poultryfarm.model.Sale;

// Holds all farm data and the business rules.
// Methods are synchronized because the server can handle several requests at once.
public class FarmManager {

    private List<Flock> flocks = new ArrayList<>();
    private List<FarmRecord> records = new ArrayList<>();

    // Next ID numbers (1, 2, 3 ...). They only go up, so an ID is never reused.
    private int nextFlockId = 1;
    private int nextRecordId = 1;

    // =========================
    // FLOCKS
    // =========================
    public synchronized List<Flock> getFlocks() {
        return new ArrayList<>(flocks);
    }

    public synchronized void addFlock(Flock flock) {

        validateFlock(flock);

        flock.setId(String.valueOf(nextFlockId++));
        flocks.add(flock);
    }

    // Returns the flock with this ID, or null when there is none
    public synchronized Flock findFlock(String id) {

        for (Flock flock : flocks) {

            if (flock.getId().equals(id)) {
                return flock;
            }
        }

        return null;
    }

    public synchronized void updateFlock(String id, Flock updatedFlock) {

        Flock existing = findFlock(id);

        if (existing == null) {
            throw new RecordNotFoundException("Flock not found.");
        }

        validateFlock(updatedFlock);

        updatedFlock.setId(id);
        flocks.set(flocks.indexOf(existing), updatedFlock);
    }

    public synchronized void deleteFlock(String id) {

        Flock flock = findFlock(id);

        if (flock == null) {
            throw new RecordNotFoundException("Flock not found.");
        }

        // Do not leave feed or egg records pointing at a flock that is gone
        for (FarmRecord record : records) {

            if (id.equals(flockIdOf(record))) {
                throw new InvalidRecordException(
                        "This flock still has feed or egg records. Delete those records first.");
            }
        }

        flocks.remove(flock);
    }

    // =========================
    // FARM RECORDS
    // =========================
    public synchronized List<FarmRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public synchronized void addRecord(FarmRecord record) {

        validateRecord(record);

        record.setId(String.valueOf(nextRecordId++));
        records.add(record);
    }

    public synchronized void updateRecord(String id, FarmRecord updatedRecord) {

        for (int i = 0; i < records.size(); i++) {

            if (records.get(i).getId().equals(id)) {

                validateRecord(updatedRecord);

                updatedRecord.setId(id);
                records.set(i, updatedRecord);
                return;
            }
        }

        throw new RecordNotFoundException("Record not found.");
    }

    public synchronized void deleteRecord(String id) {

        if (!records.removeIf(record -> record.getId().equals(id))) {
            throw new RecordNotFoundException("Record not found.");
        }
    }

    // =========================
    // VALIDATION
    // =========================
    // Throws an exception with this message when the condition is false
    private void require(boolean condition, String message) {

        if (!condition) {
            throw new InvalidRecordException(message);
        }
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private void validateFlock(Flock flock) {

        require(flock != null, "Invalid flock information.");
        require(!isBlank(flock.getType()), "Flock type is required.");
        require(flock.getQuantity() > 0, "Number of birds must be greater than zero.");
        require(flock.getAcquisitionDate() != null, "Acquisition date is required.");
    }

    private void validateRecord(FarmRecord record) {

        require(record != null, "Record cannot be empty.");
        require(record.getDate() != null, "Date is required.");

        if (record instanceof FeedRecord feed) {

            requireExistingFlock(feed.getFlockId());
            require(!isBlank(feed.getFeedType()), "Feed type is required.");
            require(feed.getBagsPurchased() >= 0 && feed.getBagsUsed() >= 0,
                    "Bags purchased and used cannot be negative.");

        } else if (record instanceof EggProduction eggs) {

            requireExistingFlock(eggs.getFlockId());
            require(eggs.getEggsCollected() > 0, "Eggs collected must be greater than zero.");

        } else if (record instanceof Sale sale) {

            require(!isBlank(sale.getItemType()), "Item sold is required.");
            require(sale.getQuantity() > 0, "Quantity must be greater than zero.");
            require(sale.getUnitPrice() >= 0, "Unit price cannot be negative.");

        } else if (record instanceof Expense expense) {

            require(!isBlank(expense.getDescription()), "Expense description is required.");
            require(expense.getAmount() > 0, "Expense amount must be greater than zero.");
        }
    }

    private void requireExistingFlock(String flockId) {
        require(!isBlank(flockId) && findFlock(flockId) != null,
                "Please choose a registered flock.");
    }

    // The flock a feed or egg record belongs to (null for sales and expenses)
    private String flockIdOf(FarmRecord record) {

        if (record instanceof FeedRecord feed) {
            return feed.getFlockId();
        }

        if (record instanceof EggProduction eggs) {
            return eggs.getFlockId();
        }

        return null;
    }

    // =========================
    // FARM STATISTICS
    // =========================
    public synchronized int getTotalBirds() {

        int total = 0;

        for (Flock flock : flocks) {
            total += flock.getQuantity();
        }

        return total;
    }

    // Adds up getAmount() of every record of one type (polymorphism):
    // eggs -> eggs collected, feed -> bags used, sales -> money in, expenses -> money out
    private double sumOf(Class<? extends FarmRecord> type) {

        double total = 0;

        for (FarmRecord record : records) {

            if (type.isInstance(record)) {
                total += record.getAmount();
            }
        }

        return total;
    }

    public synchronized int getTotalEggs() {
        return (int) sumOf(EggProduction.class);
    }

    public synchronized int getTotalFeedUsed() {
        return (int) sumOf(FeedRecord.class);
    }

    public synchronized double getTotalSales() {
        return sumOf(Sale.class);
    }

    public synchronized double getTotalExpenses() {
        return sumOf(Expense.class);
    }

    public synchronized double getProfit() {
        return getTotalSales() - getTotalExpenses();
    }

    // =========================
    // SAVING AND LOADING
    // =========================
    // The ID counters are saved with the data so numbers are never reused
    public synchronized int getNextFlockId() {
        return nextFlockId;
    }

    public synchronized int getNextRecordId() {
        return nextRecordId;
    }

    public synchronized void loadData(List<Flock> savedFlocks, List<FarmRecord> savedRecords,
            int savedNextFlockId, int savedNextRecordId) {

        flocks = savedFlocks != null ? new ArrayList<>(savedFlocks) : new ArrayList<>();
        records = savedRecords != null ? new ArrayList<>(savedRecords) : new ArrayList<>();

        nextFlockId = Math.max(savedNextFlockId, 1);
        nextRecordId = Math.max(savedNextRecordId, 1);
    }
}