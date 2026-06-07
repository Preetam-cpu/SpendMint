package com.example.demo.controller;

import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService service;
    private final UserRepository userRepo;

    public TransactionController(
            TransactionService service,
            UserRepository userRepo
    ) {
        this.service = service;
        this.userRepo = userRepo;
    }

    /* =========================
       ADD TRANSACTION
    ========================= */

    @PostMapping("/add/{email}")
    public ResponseEntity<?> addTransaction(
            @PathVariable String email,
            @RequestBody Transaction transaction
    ) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("User not found");
        }

        transaction.setUser(user);

        service.addTransaction(transaction);

        return ResponseEntity.ok("Transaction Added");
    }

    /* =========================
       GET ALL TRANSACTIONS
    ========================= */

    @GetMapping("/all/{email}")
    public ResponseEntity<?> getAllTransactions(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(
                service.getTransactionsByUser(user)
        );
    }

    /* =========================
       DELETE TRANSACTION
    ========================= */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable Long id
    ) {

        service.deleteTransaction(id);

        return ResponseEntity.ok("Deleted Successfully");
    }

    /* =========================
       UPDATE TRANSACTION
    ========================= */

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction updatedTransaction
    ) {

        Transaction updated =
                service.updateTransaction(id, updatedTransaction);

        return ResponseEntity.ok(updated);
    }

    /* =========================
       SUMMARY
    ========================= */

    @GetMapping("/summary/{email}")
    public ResponseEntity<?> getSummary(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "income", 0,
                            "expense", 0
                    )
            );
        }

        return ResponseEntity.ok(
                service.getSummaryData(user)
        );
    }

    /* =========================
       CATEGORY ANALYSIS
    ========================= */

    @GetMapping("/category-analysis/{email}")
    public ResponseEntity<?> getCategoryAnalysis(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.getCategoryAnalysis(user)
        );
    }

    /* =========================
       HEALTH SCORE
    ========================= */

    @GetMapping("/health-score/{email}")
    public ResponseEntity<?> getHealthScore(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.calculateFinancialHealthScore(user)
        );
    }

    /* =========================
       SAVING ADVICE
    ========================= */

    @GetMapping("/saving-advice/{email}")
    public ResponseEntity<?> getSavingsAdvice(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.generateSavingAdvice(user)
        );
    }

    /* =========================
       MONTHLY INCOME
    ========================= */

    @GetMapping("/monthly-income/{email}")
    public ResponseEntity<?> getMonthlyIncome(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.getMonthlyIncome(user)
        );
    }

    /* =========================
       MONTHLY EXPENSE
    ========================= */

    @GetMapping("/monthly-expense/{email}")
    public ResponseEntity<?> getMonthlyExpense(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.getMonthlyExpense(user)
        );
    }

    /* =========================
       MONTHLY SAVINGS
    ========================= */

    @GetMapping("/monthly-savings/{email}")
    public ResponseEntity<?> getMonthlySavings(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.getMonthlySavings(user)
        );
    }

    /* =========================
       TOP CATEGORY
    ========================= */

    @GetMapping("/top-category/{email}")
    public ResponseEntity<?> getTopCategory(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.getTopCategory(user)
        );
    }

    /* =========================
       BUDGET PLAN
    ========================= */

    @GetMapping("/budget-plan/{email}")
    public ResponseEntity<?> getBudgetPlan(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.generateBudgetPlan(user)
        );
    }

    /* =========================
       AI INSIGHTS
    ========================= */

    @GetMapping("/insights/{email}")
    public ResponseEntity<?> getInsights(
            @PathVariable String email
    ) {

        User user = userRepo.findByEmail(email);

        return ResponseEntity.ok(
                service.generateInsights(user)
        );
    }
}