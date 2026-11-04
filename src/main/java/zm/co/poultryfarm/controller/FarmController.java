package zm.co.poultryfarm.controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import zm.co.poultryfarm.exception.InvalidRecordException;
import zm.co.poultryfarm.exception.RecordNotFoundException;
import zm.co.poultryfarm.manager.FarmManager;
import zm.co.poultryfarm.model.EggProduction;
import zm.co.poultryfarm.model.Expense;
import zm.co.poultryfarm.model.FarmRecord;
import zm.co.poultryfarm.model.FeedRecord;
import zm.co.poultryfarm.model.Flock;
import zm.co.poultryfarm.model.Sale;
import zm.co.poultryfarm.storage.DataStorage;

public class FarmController {

    private final FarmManager manager;
    private final DataStorage storage;
    private final ObjectMapper mapper;

    public FarmController(FarmManager manager, DataStorage storage) {
        this.manager = manager;
        this.storage = storage;
        this.mapper = storage.getMapper();
    }

    // Register all API routes
    public void registerRoutes(Javalin app) {

        // Errors are sent back as JSON: {"error": "message"}
        app.exception(RecordNotFoundException.class,
                (e, ctx) -> ctx.status(404).json(Map.of("error", e.getMessage())));

        app.exception(InvalidRecordException.class,
                (e, ctx) -> ctx.status(400).json(Map.of("error", e.getMessage())));

        app.exception(JsonProcessingException.class,
                (e, ctx) -> ctx.status(400).json(Map.of("error", "The submitted data is not valid.")));

        // ----- Flocks -----
        app.get("/api/flocks", ctx -> sendJson(ctx, manager.getFlocks()));

        app.post("/api/flocks", ctx -> {

            Flock flock = mapper.readValue(ctx.body(), Flock.class);

            manager.addFlock(flock);        // the manager gives the flock its ID
            storage.save(manager);

            ctx.status(201);
            sendJson(ctx, flock);
        });

        app.put("/api/flocks/{id}", ctx -> {

            Flock flock = mapper.readValue(ctx.body(), Flock.class);

            manager.updateFlock(ctx.pathParam("id"), flock);
            storage.save(manager);

            sendJson(ctx, flock);
        });

        app.delete("/api/flocks/{id}", ctx -> {

            manager.deleteFlock(ctx.pathParam("id"));
            storage.save(manager);

            sendJson(ctx, Map.of("message", "Flock deleted successfully."));
        });

        // ----- Farm records -----
        app.get("/api/records", ctx -> {

            // Naming the element type (FarmRecord) makes Jackson add "recordType" to every record
            ctx.contentType("application/json");
            ctx.result(mapper.writerFor(new TypeReference<List<FarmRecord>>() {
            })
                    .writeValueAsString(manager.getRecords()));
        });

        app.post("/api/records/feed", ctx -> addRecord(ctx, FeedRecord.class));
        app.post("/api/records/eggs", ctx -> addRecord(ctx, EggProduction.class));
        app.post("/api/records/sales", ctx -> addRecord(ctx, Sale.class));
        app.post("/api/records/expenses", ctx -> addRecord(ctx, Expense.class));

        // The JSON for an update must include its "recordType" (feed, eggs, sale or expense)
        app.put("/api/records/{id}", ctx -> {

            FarmRecord record = mapper.readValue(ctx.body(), FarmRecord.class);

            manager.updateRecord(ctx.pathParam("id"), record);
            storage.save(manager);

            sendJson(ctx, record);
        });

        app.delete("/api/records/{id}", ctx -> {

            manager.deleteRecord(ctx.pathParam("id"));
            storage.save(manager);

            sendJson(ctx, Map.of("message", "Record deleted successfully."));
        });

        // ----- Farm summary -----
        app.get("/api/summary", ctx -> sendJson(ctx, Map.of(
                "totalFlocks", manager.getFlocks().size(),
                "totalBirds", manager.getTotalBirds(),
                "totalEggs", manager.getTotalEggs(),
                "feedUsed", manager.getTotalFeedUsed(),
                "totalSales", manager.getTotalSales(),
                "totalExpenses", manager.getTotalExpenses(),
                "profit", manager.getProfit())));
    }

    // Shared by the four "add record" routes
    private void addRecord(Context ctx, Class<? extends FarmRecord> type) throws Exception {

        FarmRecord record = mapper.readValue(ctx.body(), type);

        manager.addRecord(record);          // the manager gives the record its ID
        storage.save(manager);

        ctx.status(201);
        sendJson(ctx, record);
    }

    // Convert a Java object to JSON and send it
    private void sendJson(Context ctx, Object data) throws Exception {

        ctx.contentType("application/json");
        ctx.result(mapper.writeValueAsString(data));
    }
}