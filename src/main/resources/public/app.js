// Address of the backend API
const API = "/api";

// Shortcut for finding an HTML element by its id
const $ = id => document.getElementById(id);

// Send a request and return the JSON reply.
// If the server says something is wrong, throw an Error with its message.
async function request(url, method = "GET", data) {

    const response = await fetch(API + url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: data ? JSON.stringify(data) : undefined
    });

    const reply = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(reply.error || "Request failed (" + response.status + ")");
    }

    return reply;
}

// Show an amount in Zambian Kwacha, e.g. K25.00
function money(amount) {
    return "K" + Number(amount).toFixed(2);
}

// Put today's date (in the user's own time zone) into every date box
function setDates() {

    const offset = new Date().getTimezoneOffset() * 60000;
    const today = new Date(Date.now() - offset).toISOString().slice(0, 10);

    document.querySelectorAll('input[type="date"]').forEach(box => box.value = today);
}

// Fill a table: one row per item, one cell per value, plus a Delete button.
// getCells(item) returns the values for the row; remove(id) deletes the item.
function fillTable(tableId, items, getCells, remove) {

    const table = $(tableId);

    if (!table) {
        return;                 // this page has no such table
    }

    table.replaceChildren();

    if (items.length === 0) {
        table.innerHTML = '<tr><td colspan="5">Nothing here yet.</td></tr>';
        return;
    }

    for (const item of items) {

        const row = table.insertRow();

        for (const value of getCells(item)) {
            row.insertCell().textContent = value;
        }

        const button = document.createElement("button");
        button.textContent = "Delete";
        button.className = "delete-button";
        button.onclick = () => remove(item.id);

        row.insertCell().appendChild(button);
    }
}

// ===== Dashboard =====

// The ids of the cards on the page have the same names as the summary data
function showSummary(summary) {

    const moneyCards = ["totalSales", "totalExpenses", "profit"];

    for (const name in summary) {
        $(name).textContent = moneyCards.includes(name) ? money(summary[name]) : summary[name];
    }
}

// ===== Flocks =====

function showFlocks(flocks) {

    // Fill the Flock drop-down lists (feed and egg pages)
    for (const id of ["feedFlock", "eggFlock"]) {

        const list = $(id);

        if (!list) {
            continue;
        }

        list.replaceChildren();

        if (flocks.length === 0) {
            list.add(new Option("Register a flock first", ""));
        }

        for (const flock of flocks) {
            list.add(new Option(`Flock ${flock.id} - ${flock.type} (${flock.quantity} birds)`, flock.id));
        }
    }

    // Fill the table of flocks (flocks page)
    fillTable("flocksTable", flocks,
            flock => [flock.id, flock.type, flock.quantity, flock.acquisitionDate],
            id => remove("/flocks", id));
}

// ===== Farm records =====

const typeNames = { feed: "Feed", eggs: "Eggs", sale: "Sale", expense: "Expense" };

function describe(record) {

    if (record.recordType === "sale") {
        return `${record.summary} at ${money(record.unitPrice)} each = ${money(record.amount)}`;
    }

    if (record.recordType === "expense") {
        return `${record.summary} - ${money(record.amount)}`;
    }

    return record.summary;
}

function showRecords(records) {

    // The server keeps the records in the order they were added, so the IDs show as 1, 2, 3 ...
    fillTable("recordsTable", records,
            record => [record.id, typeNames[record.recordType], describe(record), record.date],
            id => remove("/records", id));
}

// ===== Loading and deleting =====

// Load only the data that the current page needs
async function loadPage() {

    try {
        if ($("totalFlocks")) {
            showSummary(await request("/summary"));
        }

        if ($("flocksTable") || $("feedFlock") || $("eggFlock")) {
            showFlocks(await request("/flocks"));
        }

        if ($("recordsTable")) {
            showRecords(await request("/records"));
        }

    } catch (error) {
        alert("Could not load farm data: " + error.message);
    }
}

// Delete a flock or a record (path is "/flocks" or "/records")
async function remove(path, id) {

    if (!confirm("Are you sure you want to delete this?")) {
        return;
    }

    try {
        await request(path + "/" + id, "DELETE");
        loadPage();

    } catch (error) {
        alert(error.message);
    }
}

// ===== Forms =====

// For each form: where to send it, and which input box holds each field.
// { field name sent to the server : id of the input box }
const forms = {
    flockForm: ["/flocks", { type: "flockType", quantity: "flockQuantity", acquisitionDate: "flockDate" }],
    feedForm: ["/records/feed", { flockId: "feedFlock", feedType: "feedType", bagsPurchased: "bagsPurchased", bagsUsed: "bagsUsed", date: "feedDate" }],
    eggForm: ["/records/eggs", { flockId: "eggFlock", eggsCollected: "eggsCollected", date: "eggDate" }],
    saleForm: ["/records/sales", { itemType: "itemType", quantity: "saleQuantity", unitPrice: "unitPrice", date: "saleDate" }],
    expenseForm: ["/records/expenses", { description: "expenseDescription", amount: "expenseAmount", date: "expenseDate" }]
};

for (const formId in forms) {

    const form = $(formId);

    if (!form) {
        continue;               // this page does not have this form
    }

    const [url, fields] = forms[formId];

    form.onsubmit = async event => {

        event.preventDefault();

        // Build the data to send from the input boxes
        const data = {};

        for (const name in fields) {
            const box = $(fields[name]);
            data[name] = box.type === "number" ? Number(box.value) : box.value;
        }

        try {
            await request(url, "POST", data);

            alert("Saved successfully.");

            form.reset();
            setDates();
            loadPage();

        } catch (error) {
            alert(error.message);
        }
    };
}

// When the page opens
setDates();
loadPage();