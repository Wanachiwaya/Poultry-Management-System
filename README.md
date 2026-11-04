# Poultry Management System

Object-Oriented Programming Group Project, Zambia University College of Technology.

## Team Members

| No. | Full Name | Student Number |
| --- | --- | --- |
| 1 | Wana Chiwaya | 2421052 |
| 2 | Musonda Musonda | 2421057 |
| 3 | Mambwe Ngoma | 2420911 |
| 4 | Gift Chungu | 2420906 |
| 5 | Monde Lubasi | 2421056 |

## 1. Project Description

The Poultry Management System is a Java-based application designed to help small-scale poultry farmers manage their farm activities and records.

The system allows farmers to record flock information, feed usage, egg production, sales and expenses.

It also calculates total sales, expenses and farm profit or loss.

## 2. Technologies Used

* Java 17 or later
* Maven
* Javalin 6 (web server)
* Jackson (JSON)
* HTML, CSS, JavaScript (frontend)
* JSON file for data storage

## 3. Main Features

* Register and delete poultry flocks.
* Record feed purchases and usage.
* Record daily egg production.
* Record sales of eggs and chickens.
* Record farm expenses.
* Calculate farm profit or loss.
* Save farm information in a JSON file and load it again on restart.
* Input validation with clear error messages.

## 4. Project Structure

```
poultry-management-system/
├── pom.xml
└── src/main/
    ├── java/zm/co/poultryfarm/
    │   ├── Main.java            starts the server
    │   ├── model/               Flock, FeedRecord, EggProduction, Sale, Expense, FarmRecord
    │   ├── manager/             FarmManager (business rules, validation, totals)
    │   ├── controller/          FarmController (API routes)
    │   ├── storage/             DataStorage (JSON save/load)
    │   └── exception/           InvalidRecordException, RecordNotFoundException
    └── resources/public/        the web pages (one HTML file per page), style.css, app.js
```

### Main classes

| Class | Responsibility |
| --- | --- |
| `Flock` | A group of chickens: ID, type (broiler or layer), number of birds, acquisition date |
| `FarmRecord` (interface) | What every record can do: `getId()`, `getDate()`, `getSummary()`, `getAmount()` |
| `FeedRecord` | Feed bags purchased and used for a flock (implements `FarmRecord`) |
| `EggProduction` | Eggs collected from a flock on a day (implements `FarmRecord`) |
| `Sale` | A sale of eggs or chickens: quantity and unit price (implements `FarmRecord`) |
| `Expense` | A farm cost: description and amount (implements `FarmRecord`) |
| `FarmManager` | Keeps the lists of flocks and records, validates them, gives IDs 1, 2, 3 ... and calculates totals and profit |
| `FarmController` | The Javalin REST endpoints (GET, POST, PUT, DELETE) |
| `DataStorage` | Saves and loads the data in a JSON file |
| `InvalidRecordException`, `RecordNotFoundException` | Custom exceptions for bad input (HTTP 400) and missing items (HTTP 404) |

**OOP ideas used:** encapsulation (private fields with getters and setters), an interface (`FarmRecord`) implemented by four classes, polymorphism (`getAmount()` and `getSummary()` behave differently for each record type), custom exceptions, and collections (`ArrayList`).

## 5. How to Run the Project

1. Install Java 17 or later and Apache Maven.
2. Open a terminal in the project folder (the one containing `pom.xml`).
3. Compile: `mvn clean compile`
4. Start: `mvn exec:java`
5. Open `http://localhost:7070` in your browser.

To use a different port, set the `PORT` variable first, e.g. `PORT=8080 mvn exec:java`
(Windows PowerShell: `$env:PORT=8080; mvn exec:java`).

## 6. API Endpoints

**Flock Management**

* GET `/api/flocks` - View all registered flocks.
* POST `/api/flocks` - Register a new flock.
* PUT `/api/flocks/{id}` - Update flock information.
* DELETE `/api/flocks/{id}` - Delete a flock (only when it has no feed/egg records).

**Farm Records**

* GET `/api/records` - View all farm records.
* POST `/api/records/feed` - Add a feed record.
* POST `/api/records/eggs` - Record egg production.
* POST `/api/records/sales` - Record a sale.
* POST `/api/records/expenses` - Record a farm expense.
* PUT `/api/records/{id}` - Update a farm record (the JSON must include `"recordType"`: `feed`, `eggs`, `sale` or `expense`).
* DELETE `/api/records/{id}` - Delete a farm record.

**Farm Summary**

* GET `/api/summary` - Total flocks, birds, eggs, feed used, sales, expenses and profit, plus eggs in stock (collected minus sold) and feed bags in stock (bought minus used).

Errors are returned as JSON, e.g. `{"error": "Date is required."}`, with status 400 (invalid data), 404 (not found) or 500 (server problem).

## 7. Data Storage

Farm information is stored in `data/poultry-data.json`, created automatically the first time you save something. Delete this file to start with a clean farm.

**IDs:** flocks are numbered 1, 2, 3 ... and records are numbered 1, 2, 3 ... separately. The server gives a new item the highest existing number plus 1, so the first item is 1 and the numbering starts again at 1 when the list is empty.

## 8. Project Objective

To give small-scale poultry farmers a simple and organised way to manage daily farm activities, monitor production and track expenses.

## 9. Classes

| Class | Responsibility |
| --- | --- |
| `Flock` | A group of chickens: ID, type (broiler or layer), number of birds, acquisition date. |
| `FarmRecord` (interface) | What every record can do: `getId()`, `getDate()`, `getSummary()`, `getAmount()`. |
| `FeedRecord` | Feed bags bought and used for a flock. |
| `EggProduction` | Eggs collected from a flock on a day. |
| `Sale` | A sale of eggs or chickens: quantity and unit price. |
| `Expense` | A farm cost: description and amount. |
| `FarmManager` | Keeps the lists of flocks and records, validates them, gives them IDs 1, 2, 3 ... and calculates the totals. |
| `FarmController` | The Javalin REST endpoints that connect the web pages to `FarmManager`. |
| `DataStorage` | Saves and loads all farm data in `data/poultry-data.json`. |
| `InvalidRecordException` | Thrown for bad input, such as a negative quantity (HTTP 400). |
| `RecordNotFoundException` | Thrown when a flock or record does not exist (HTTP 404). Extends `InvalidRecordException`. |
| `Main` | Starts the web server on port 7070. |

**OOP in this project:** `FeedRecord`, `EggProduction`, `Sale` and `Expense` implement `FarmRecord` (interface), so `FarmManager` holds them in one list. Calling `getAmount()` on each behaves differently (polymorphism). Fields are private with getters and setters (encapsulation).