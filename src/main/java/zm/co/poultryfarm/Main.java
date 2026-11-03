package zm.co.poultryfarm;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import zm.co.poultryfarm.controller.FarmController;
import zm.co.poultryfarm.manager.FarmManager;
import zm.co.poultryfarm.storage.DataStorage;

public class Main {

    public static void main(String[] args) {

        // Display welcome message
        System.out.println("Welcome to the Poultry Management System!");

        // Create the farm manager
        FarmManager manager = new FarmManager();

        // Create the storage system
        DataStorage storage = new DataStorage();

        // Load previously saved farm information
        storage.load(manager);

        // Create the Javalin web server
        Javalin app = Javalin.create(config -> {

            // Make frontend files accessible through the browser
            config.staticFiles.add("/public", Location.CLASSPATH);

        });

        // Connect the API controller to the server
        FarmController controller = new FarmController(manager, storage);

        // Register all API routes
        controller.registerRoutes(app);

        // Start the server on port 7070 (set the PORT variable to change it)
        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "7070"));

        app.start(port);

        System.out.println("Server started successfully!");
        System.out.println("Open http://localhost:" + port + " in your browser.");
    }
}
