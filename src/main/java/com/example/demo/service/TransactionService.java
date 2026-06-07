package com.example.demo.service;

import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository repo;

    public TransactionService(
            TransactionRepository repo
    ){

        this.repo = repo;
    }

    /* =========================
       ADD TRANSACTION
    ========================= */

    public String addTransaction(
            Transaction transaction
    ){

        repo.save(transaction);

        return "Transaction Saved!";
    }

    /* =========================
       GET USER TRANSACTIONS
    ========================= */

    public List<Transaction> getTransactionsByUser(
            User user
    ){

        return repo.findByUser(user);
    }

    /* =========================
       DELETE TRANSACTION
    ========================= */

    public void deleteTransaction(Long id){

        repo.deleteById(id);
    }

    /* =========================
       UPDATE TRANSACTION
    ========================= */

    public Transaction updateTransaction(

            Long id,

            Transaction updatedTransaction
    ){

        Transaction transaction =
                repo.findById(id).orElseThrow();

        transaction.setAmount(
                updatedTransaction.getAmount()
        );

        transaction.setType(
                updatedTransaction.getType()
        );

        transaction.setCategory(
                updatedTransaction.getCategory()
        );

        transaction.setDate(
                updatedTransaction.getDate()
        );

        return repo.save(transaction);
    }

    /* =========================
       SUMMARY
    ========================= */

    public Map<String, Double> getSummaryData(
            User user
    ){

        List<Transaction> transactions =
                repo.findByUser(user);

        double income = 0;

        double expense = 0;

        for(Transaction t : transactions){

            if(
                    t.getType()
                            .equalsIgnoreCase("income")
            ){

                income += t.getAmount();

            }else{

                expense += t.getAmount();
            }
        }

        Map<String, Double> summary =
                new HashMap<>();

        summary.put("Income", income);

        summary.put("Expense", expense);

        return summary;
    }

    /* =========================
       CATEGORY ANALYSIS
    ========================= */

    public Map<String, Double> getCategoryAnalysis(
            User user
    ){

        List<Transaction> transactions =
                repo.findByUser(user);

        Map<String, Double> categoryMap =
                new HashMap<>();

        for(Transaction t : transactions){

            if(
                    t.getType()
                            .equalsIgnoreCase("expense")
            ){

                categoryMap.put(

                        t.getCategory(),

                        categoryMap.getOrDefault(
                                t.getCategory(),
                                0.0
                        ) + t.getAmount()
                );
            }
        }

        return categoryMap;
    }

    /* =========================
       HEALTH SCORE
    ========================= */

    public int calculateFinancialHealthScore(
            User user
    ){

        List<Transaction> transactions =
                repo.findByUser(user);

        double income = 0;

        double expense = 0;

        for(Transaction t : transactions){

            if(
                    t.getType()
                            .equalsIgnoreCase("income")
            ){

                income += t.getAmount();

            }else{

                expense += t.getAmount();
            }
        }

        if(income == 0){

            return 0;
        }

        double ratio =
                (expense / income) * 100;

        if(ratio <= 50){

            return 90;

        }else if(ratio <= 70){

            return 75;

        }else if(ratio <= 90){

            return 50;

        }else{

            return 30;
        }
    }

    /* =========================
       SAVING ADVICE
    ========================= */

    public String generateSavingAdvice(
            User user
    ){

        int score =
                calculateFinancialHealthScore(user);

        if(score >= 80){

            return "Excellent savings habit!";

        }else if(score >= 60){

            return "Good, but reduce unnecessary expenses.";

        }else{

            return "Your spending is high. Focus on saving more.";
        }
    }

    /* =========================
       OVERSPENDING ALERT
    ========================= */

    public String detectOverspending(
            User user
    ){

        List<Transaction> transactions =
                repo.findByUser(user);

        double income = 0;

        double expense = 0;

        for(Transaction t : transactions){

            if(
                    t.getType()
                            .equalsIgnoreCase("income")
            ){

                income += t.getAmount();

            }else{

                expense += t.getAmount();
            }
        }

        if(expense > income){

            return "⚠ Overspending Alert! Expenses exceed income.";

        }else{

            return "✅ Spending is under control.";
        }
    }


    /* =========================
   MONTHLY INCOME
========================= */

    public Map<String ,Double> getMonthlyIncome(User user){
        List<Transaction> transactions=repo.findByUser(user);

        Map<String,Double> monthlyIncome=new HashMap<>();
        for(Transaction t: transactions){
            if(t.getType().equalsIgnoreCase("income")){
                String month=t.getDate().substring(0,7);
                monthlyIncome.put(
                        month,
                        monthlyIncome.getOrDefault(
                                month,
                                0.0
                        )+ t.getAmount()
                );

            }
        }
        return monthlyIncome;
    }

    /* =========================
   MONTHLY EXPENSE
========================= */
    public Map<String,Double> getMonthlyExpense(User user){
        List<Transaction> transactions=repo.findByUser(user);

        Map<String,Double> monthlyExpense=new HashMap<>();
        for(Transaction t: transactions){
            if(t.getType().equalsIgnoreCase("expense")){
                String month=t.getDate().substring(0,7);

                monthlyExpense.put(
                        month,
                        monthlyExpense.getOrDefault(
                                month,
                                0.0
                        )+ t.getAmount()
                );
            }
        }
        return monthlyExpense;
    }
/* =========================
   MONTHLY SAVINGS
========================= */

    public Map<String,Double> getMonthlySavings(User user){

        Map<String,Double> income=getMonthlyIncome(user);
        Map<String,Double> expense=getMonthlyExpense(user);
        Map<String,Double> savings=new HashMap<>();
        for(String month:income.keySet()){
            double incomeValue=income.getOrDefault(month,0.0);
            double expenseValue=expense.getOrDefault(month,0.0);
            savings.put(
                    month,
                    incomeValue - expenseValue
            );

        }
        return savings;
    }

/* =========================
   TOP SPENDING CATEGORY
========================= */

    public String getTopCategory(User user){
        Map<String,Double> categories=getCategoryAnalysis(user);
        String topCategory="None";
        double max=0;
        for(String category: categories.keySet()){
            if(categories.get(category)>max){
                max=categories.get(category);
                topCategory =category;
            }
        }
        return topCategory + " : ₹" + max;
    }

/* =========================
   SMART BUDGET RECOMMENDATION
========================= */
    public Map<String,Double > generateBudgetPlan(User user){
        List<Transaction> transactions=repo.findByUser(user);
        double income=0;
        for(Transaction  t: transactions){
            if(t.getType().equalsIgnoreCase("income")){
                income+=t.getAmount();
            }
        }
        Map<String,Double> budget=new HashMap<>();
        budget.put("Food",
                income*0.10);
        budget.put(
                "Transport",
                income*.10
        );
        budget.put("Entertainment",
                income*0.30);
        budget.put(
                "Savings",
                income*0.30
        );
        budget.put(
                "Others",
                income*0.15
        );
        return budget;


    }

/* =========================
   ANOMALY DETECTION
========================= */
    public String detectAnomaly(User user){
        Map<String,Double> categories=getCategoryAnalysis(user);
        double total=0;
        for(double amount:categories.values()){
            total +=amount;

        }
        for(String category:categories.keySet()){
            double percentage=(categories.get(category)/total)*100;
            if(percentage> 50){
                return "⚠ High spending detected in "
                        + category;
            }

        }
        return "✅ Spending distribution looks healthy.";
    }

/* =========================
   AI INSIGHTS
========================= */
    public List<String> generateInsights(User user){
        List<String> insights=new ArrayList<>();
        Map<String,Double> categories=getCategoryAnalysis(user);
        for(String category: categories.keySet()){
            if(categories.get(category)>10000){
                insights.add(
                        "⚠ High spending on "
                                + category
                );
            }
        }
        if(insights.isEmpty()){
            insights.add(
                    "✅ Financial activity looks stable."
            );

        }
        return insights;
    }

}