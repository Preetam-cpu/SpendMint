let editingId = null;

let expenseChart = null;
let incomeChart = null;
let expenseTrendChart = null;
let savingsChart = null;
let budgetChart = null;

/* =========================
   LOGIN CHECK
========================= */

const loggedInUser = localStorage.getItem("loggedInUser");

if (!loggedInUser) {
    window.location.href = "login.html";
}

/* =========================
   SHOW NOTIFICATION
========================= */

function showNotification(message, type = "success") {

    const notification = document.createElement("div");

    notification.className = `notification ${type}`;

    notification.innerText = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.classList.add("show");
    }, 100);

    setTimeout(() => {

        notification.classList.remove("show");

        setTimeout(() => {
            notification.remove();
        }, 300);

    }, 2500);
}

/* =========================
   ADD / UPDATE TRANSACTION
========================= */

async function addTransaction() {

    const transaction = {

        amount: document.getElementById("amount").value,

        type: document.getElementById("type").value,

        category: document.getElementById("category").value,

        date: document.getElementById("date").value
    };

    if (
        transaction.amount === "" ||
        transaction.category === "" ||
        transaction.date === ""
    ) {

        showNotification("Fill all fields", "error");
        return;
    }

    let url =
        `http://localhost:8080/transactions/add/${loggedInUser}`;

    let method = "POST";

    if (editingId != null) {

        url =
            `http://localhost:8080/transactions/${editingId}`;

        method = "PUT";
    }

    try {

        const response = await fetch(url, {

            method: method,

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(transaction)
        });

        if (!response.ok) {
            throw new Error();
        }

        editingId = null;

        clearForm();

        await reloadDashboard();

        showNotification("Transaction Saved");

    } catch (error) {

        showNotification("Transaction Failed", "error");
    }
}

/* =========================
   LOAD TRANSACTIONS
========================= */

async function loadTransactions() {

    const response = await fetch(
        `http://localhost:8080/transactions/all/${loggedInUser}`
    );

    const data = await response.json();

    let output = "";

    data.forEach(t => {

        output += `
        <tr>

            <td>${t.id}</td>

            <td>₹ ${t.amount}</td>

            <td>
                <span class="${
            t.type === "income"
                ? "income-text"
                : "expense-text"
        }">
                    ${t.type}
                </span>
            </td>

            <td>${t.category}</td>

            <td>${t.date}</td>

            <td>

                <div class="action-buttons">

                    <button
                        class="edit-btn"
                        onclick="editTransaction(
                            ${t.id},
                            '${t.amount}',
                            '${t.type}',
                            '${t.category}',
                            '${t.date}'
                        )">
                        Edit
                    </button>

                    <button
                        class="delete-btn"
                        onclick="deleteTransaction(${t.id})">
                        Delete
                    </button>

                </div>

            </td>

        </tr>
        `;
    });

    if (data.length === 0) {

        output = `
        <tr>
            <td colspan="6">
                No Transactions Found
            </td>
        </tr>
        `;
    }

    document.getElementById("transactions").innerHTML = output;
}

/* =========================
   DELETE
========================= */

async function deleteTransaction(id) {

    const confirmDelete =
        confirm("Delete transaction?");

    if (!confirmDelete) return;

    await fetch(
        `http://localhost:8080/transactions/${id}`,
        {
            method: "DELETE"
        }
    );

    await reloadDashboard();

    showNotification("Transaction Deleted");
}

/* =========================
   EDIT
========================= */

function editTransaction(
    id,
    amount,
    type,
    category,
    date
) {

    editingId = id;

    document.getElementById("amount").value = amount;

    document.getElementById("type").value = type;

    document.getElementById("category").value = category;

    document.getElementById("date").value = date;

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });

    showNotification("Edit Mode Enabled");
}

/* =========================
   SUMMARY
========================= */

async function loadSummary() {

    const response = await fetch(
        `http://localhost:8080/transactions/summary/${loggedInUser}`
    );

    const data = await response.json();

    const income = data.Income || 0;

    const expense = data.Expense || 0;

    const balance = income - expense;

    document.getElementById("income").innerHTML =
        `₹ ${income}`;

    document.getElementById("expense").innerHTML =
        `₹ ${expense}`;

    document.getElementById("balance").innerHTML =
        `₹ ${balance}`;
}

/* =========================
   HEALTH SCORE
========================= */

async function loadHealthScore() {

    const response = await fetch(
        `http://localhost:8080/transactions/health-score/${loggedInUser}`
    );

    const score = await response.text();

    document.getElementById(
        "healthScore"
    ).innerHTML = `${score}/100`;
}

/* =========================
   SAVING ADVICE
========================= */

async function loadSavingAdvice() {

    const response = await fetch(
        `http://localhost:8080/transactions/saving-advice/${loggedInUser}`
    );

    const advice = await response.text();

    document.getElementById(
        "savingAdvice"
    ).innerHTML = advice;
}

/* =========================
   CATEGORY CHART
========================= */

async function loadCategoryChart() {

    const response = await fetch(
        `http://localhost:8080/transactions/category-analysis/${loggedInUser}`
    );

    const data = await response.json();

    const labels = Object.keys(data);

    const values = Object.values(data);

    const ctx =
        document.getElementById("expenseChart");

    if (expenseChart) {
        expenseChart.destroy();
    }

    expenseChart = new Chart(ctx, {

        type: "doughnut",

        data: {

            labels: labels,

            datasets: [{

                data: values,

                backgroundColor: [
                    "#3b82f6",
                    "#22c55e",
                    "#ef4444",
                    "#f59e0b",
                    "#8b5cf6"
                ]
            }]
        }
    });
}

/* =========================
   MONTHLY INCOME CHART
========================= */

async function loadMonthlyIncomeChart() {

    const response = await fetch(
        `http://localhost:8080/transactions/monthly-income/${loggedInUser}`
    );

    const data = await response.json();

    if (incomeChart) {
        incomeChart.destroy();
    }

    incomeChart = new Chart(

        document.getElementById("incomeChart"),

        {
            type: "line",

            data: {

                labels: Object.keys(data),

                datasets: [{

                    label: "Income",

                    data: Object.values(data),

                    borderColor: "#22c55e",

                    tension: 0.4
                }]
            }
        }
    );
}

/* =========================
   MONTHLY EXPENSE CHART
========================= */

async function loadMonthlyExpenseChart() {

    const response = await fetch(
        `http://localhost:8080/transactions/monthly-expense/${loggedInUser}`
    );

    const data = await response.json();

    if (expenseTrendChart) {
        expenseTrendChart.destroy();
    }

    expenseTrendChart = new Chart(

        document.getElementById("expenseTrendChart"),

        {
            type: "bar",

            data: {

                labels: Object.keys(data),

                datasets: [{

                    label: "Expenses",

                    data: Object.values(data),

                    backgroundColor: "#ef4444"
                }]
            }
        }
    );
}

/* =========================
   SAVINGS CHART
========================= */

async function loadSavingsChart() {

    const response = await fetch(
        `http://localhost:8080/transactions/monthly-savings/${loggedInUser}`
    );

    const data = await response.json();

    if (savingsChart) {
        savingsChart.destroy();
    }

    savingsChart = new Chart(

        document.getElementById("savingsChart"),

        {
            type: "line",

            data: {

                labels: Object.keys(data),

                datasets: [{

                    label: "Savings",

                    data: Object.values(data),

                    borderColor: "#3b82f6",

                    tension: 0.4
                }]
            }
        }
    );
}

/* =========================
   BUDGET CHART
========================= */

async function loadBudgetChart() {

    const response = await fetch(
        `http://localhost:8080/transactions/budget-plan/${loggedInUser}`
    );

    const data = await response.json();

    if (budgetChart) {
        budgetChart.destroy();
    }

    budgetChart = new Chart(

        document.getElementById("budgetChart"),

        {
            type: "pie",

            data: {

                labels: Object.keys(data),

                datasets: [{

                    data: Object.values(data)
                }]
            }
        }
    );
}

/* =========================
   TOP CATEGORY
========================= */

async function loadTopCategory() {

    const response = await fetch(
        `http://localhost:8080/transactions/top-category/${loggedInUser}`
    );

    const data = await response.text();

    document.getElementById(
        "topCategory"
    ).innerHTML = data;
}

/* =========================
   INSIGHTS
========================= */

async function loadInsights() {

    const response = await fetch(
        `http://localhost:8080/transactions/insights/${loggedInUser}`
    );

    const data = await response.json();

    let output = "";

    data.forEach(insight => {

        output += `
        <li>${insight}</li>
        `;
    });

    document.getElementById(
        "insightsList"
    ).innerHTML = output;
}

/* =========================
   SEARCH
========================= */

function searchTransactions() {

    const input =
        document.getElementById("searchInput")
            .value
            .toLowerCase();

    const rows =
        document.querySelectorAll("#transactions tr");

    rows.forEach(row => {

        const text =
            row.innerText.toLowerCase();

        row.style.display =
            text.includes(input)
                ? ""
                : "none";
    });
}

/* =========================
   EXPORT CSV
========================= */

function exportCSV() {

    let csv = [];

    const rows =
        document.querySelectorAll("table tr");

    rows.forEach(row => {

        const cols =
            row.querySelectorAll("td, th");

        let rowData = [];

        cols.forEach(col => {

            rowData.push(col.innerText);
        });

        csv.push(rowData.join(","));
    });

    const csvFile = new Blob(
        [csv.join("\n")],
        {
            type: "text/csv"
        }
    );

    const downloadLink =
        document.createElement("a");

    downloadLink.download =
        "transactions.csv";

    downloadLink.href =
        window.URL.createObjectURL(csvFile);

    downloadLink.click();
}

/* =========================
   CLEAR FORM
========================= */

function clearForm() {

    document.getElementById("amount").value = "";

    document.getElementById("type").value = "income";

    document.getElementById("category").value = "";

    document.getElementById("date").value = "";
}

/* =========================
   GOAL
========================= */

function calculateGoalProgress() {

    const goal =
        document.getElementById("savingGoal").value;

    const income =
        parseFloat(
            document.getElementById("income")
                .innerText.replace("₹", "")
        );

    const expense =
        parseFloat(
            document.getElementById("expense")
                .innerText.replace("₹", "")
        );

    const savings = income - expense;

    const progress =
        Math.min(
            ((savings / goal) * 100),
            100
        ).toFixed(1);
    document.getElementById(
        "goalProgress"
    ).innerHTML =
        `Goal Progress: ${progress}%`;
}

/* =========================
   LOGOUT
========================= */

function logout() {

    localStorage.removeItem("loggedInUser");

    window.location.href = "login.html";
}

/* =========================
   RELOAD DASHBOARD
========================= */

async function reloadDashboard() {

    await loadTransactions();

    await loadSummary();

    await loadHealthScore();

    await loadSavingAdvice();

    await loadCategoryChart();

    await loadMonthlyIncomeChart();

    await loadMonthlyExpenseChart();

    await loadSavingsChart();

    await loadBudgetChart();

    await loadTopCategory();

    await loadInsights();
}


/* =========================
   SECTION NAVIGATION
========================= */

function showSection(sectionId, element) {

    const sections =
        document.querySelectorAll(".page-section");

    sections.forEach(section => {
        section.classList.add("hidden-section");
    });

    document
        .getElementById(sectionId)
        .classList.remove("hidden-section");

    const menuItems =
        document.querySelectorAll(".menu-item");

    menuItems.forEach(item => {
        item.classList.remove("active");
    });

    element.classList.add("active");

    // IMPORTANT FIX
    if(sectionId === "analyticsSection") {

        setTimeout(() => {

            loadCategoryChart();
            loadMonthlyIncomeChart();
            loadMonthlyExpenseChart();
            loadSavingsChart();
            loadBudgetChart();

        }, 200);
    }
}

/* =========================
   THEME TOGGLE
========================= */

function toggleTheme() {

    document.body.classList.toggle("light-mode");

    localStorage.setItem(
        "theme",
        document.body.classList.contains("light-mode")
            ? "light"
            : "dark"
    );
}

/* =========================
   LOAD THEME
========================= */

window.onload = () => {

    const theme =
        localStorage.getItem("theme");

    if (theme === "light") {

        document.body.classList.add("light-mode");
    }

    const today = new Date();

    document.getElementById(
        "currentDate"
    ).innerHTML =
        today.toDateString();
};


/* =========================
   INITIAL LOAD
========================= */

reloadDashboard();