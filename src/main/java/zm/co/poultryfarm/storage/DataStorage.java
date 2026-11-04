package zm.co.poultryfarm.storage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import zm.co.poultryfarm.manager.FarmManager;
import zm.co.poultryfarm.model.FarmRecord;
import zm.co.poultryfarm.model.Flock;

public class DataStorage {

    // Location where farm information is saved
    private final File file = new File("data/poultry-data.json");

    // The ObjectMapper converts Java objects to JSON and back
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule()) // lets Jackson handle LocalDate
            .enable(SerializationFeature.INDENT_OUTPUT) // easy-to-read JSON file
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // "2026-11-02", not [2026, 11, 2]
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // ignore "summary" and "amount" when reading
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL) // accept JSON without "recordType"
            .build();

    // Save farm information to the JSON file
    public synchronized void save(FarmManager manager) {

        try {
            file.getParentFile().mkdirs();

            mapper.writeValue(file, new SavedData(
                    manager.getFlocks(),
                    manager.getRecords(),
                    manager.getNextFlockId(),
                    manager.getNextRecordId()));

        } catch (IOException e) {
            throw new RuntimeException("Could not save farm data.", e);
        }
    }

    // Load previously saved information (start empty if there is no file yet)
    public synchronized void load(FarmManager manager) {

        if (!file.exists()) {
            return;
        }

        try {
            SavedData data = mapper.readValue(file, SavedData.class);

            List<Flock> flocks = data.flocks != null ? data.flocks : List.of();
            List<FarmRecord> records = data.records != null ? data.records : List.of();

            // Old save files have no counters (they read as 0),
            // so work them out from the saved data instead
            int nextFlockId = data.nextFlockId > 0
                    ? data.nextFlockId
                    : highestId(flocks.stream().map(Flock::getId).toList()) + 1;

            int nextRecordId = data.nextRecordId > 0
                    ? data.nextRecordId
                    : highestId(records.stream().map(FarmRecord::getId).toList()) + 1;

            manager.loadData(flocks, records, nextFlockId, nextRecordId);

        } catch (IOException e) {
            throw new RuntimeException("Could not load farm data from " + file.getPath(), e);
        }
    }

    // Make the mapper available to the controller
    public ObjectMapper getMapper() {
        return mapper;
    }

    // Highest numeric ID in a list of ID strings (0 if the list is empty)
    private int highestId(List<String> ids) {

        int max = 0;

        for (String id : ids) {
            try {
                max = Math.max(max, Integer.parseInt(id));
            } catch (NumberFormatException ignored) {
                // skip IDs that are not numbers
            }
        }

        return max;
    }

    // Groups everything that is saved in the file
    public static class SavedData {

        public List<Flock> flocks;
        public List<FarmRecord> records;
        public int nextFlockId;
        public int nextRecordId;

        public SavedData() {
        }

        public SavedData(List<Flock> flocks, List<FarmRecord> records,
                int nextFlockId, int nextRecordId) {
            this.flocks = flocks;
            this.records = records;
            this.nextFlockId = nextFlockId;
            this.nextRecordId = nextRecordId;
        }
    }
}
