package database;

import banking.Customer;
import banking.TransactionRecord;
import logging.NetworkLogEntry;

import java.io.File;
import java.util.List;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 10 TEST: SQLITE DATABASE & TRANSACTIONAL LEDGER        ");
        System.out.println("================================================================");

        // Use a test SQLite file
        String testDbFile = "test_banking.db";
        File f = new File(testDbFile);
        if (f.exists()) f.delete();

        DatabaseConnection dbConn = new DatabaseConnection("jdbc:sqlite:" + testDbFile);
        DatabaseInitializer initializer = new DatabaseInitializer(dbConn);
        BankingRepository repo = new BankingRepository(dbConn);

        int passed = 0;
        int total = 0;

        try {
            // Test 1: Initialize DB and verify seeded customers
            total++;
            initializer.initializeDatabase();
            List<Customer> customers = repo.getAllCustomers();
            System.out.println("[TEST 1] Initialized Database. Seeded customers count: " + customers.size() + " (Expected: 4)");
            boolean t1Ok = customers.size() == 4;
            System.out.printf("         Result: %s\n", t1Ok ? "PASS" : "FAIL");
            if (t1Ok) passed++;

            // Test 2: Balance Inquiry
            total++;
            Double aliceBal = repo.getBalance("ACC-100234");
            System.out.printf("[TEST 2] Alice Balance: ₹%.2f (Expected: 50000.00) -> %s\n",
                    aliceBal, (aliceBal != null && aliceBal == 50000.00) ? "PASS" : "FAIL");
            if (aliceBal != null && aliceBal == 50000.00) passed++;

            // Test 3: Successful Withdrawal of ₹2,000
            total++;
            BankingRepository.TransactionResult w1 = repo.processWithdrawal("ACC-100234", 2000.00, "ATM-01");
            System.out.printf("[TEST 3] Withdrawal ₹2000: Status=%s, NewBal=₹%.2f (Expected: 48000.00) -> %s\n",
                    w1.responseCode, w1.newBalance, (w1.success && w1.newBalance == 48000.00) ? "PASS" : "FAIL");
            if (w1.success && w1.newBalance == 48000.00) passed++;

            // Test 4: Insufficient Funds Rejection
            total++;
            BankingRepository.TransactionResult w2 = repo.processWithdrawal("ACC-100234", 100000.00, "ATM-01");
            System.out.printf("[TEST 4] Insufficient Funds Rejection: Status=%s, Message=%s -> %s\n",
                    w2.responseCode, w2.message, (!w2.success && "ERR_INSUFFICIENT_FUNDS".equals(w2.responseCode)) ? "PASS" : "FAIL");
            if (!w2.success && "ERR_INSUFFICIENT_FUNDS".equals(w2.responseCode)) passed++;

            // Test 5: Invalid Negative Amount Rejection
            total++;
            BankingRepository.TransactionResult w3 = repo.processWithdrawal("ACC-100234", -500.00, "ATM-01");
            System.out.printf("[TEST 5] Reject Negative Amount: Status=%s -> %s\n",
                    w3.responseCode, (!w3.success && "ERR_INVALID_AMOUNT".equals(w3.responseCode)) ? "PASS" : "FAIL");
            if (!w3.success && "ERR_INVALID_AMOUNT".equals(w3.responseCode)) passed++;

            // Test 6: Deposit Transaction
            total++;
            BankingRepository.TransactionResult d1 = repo.processDeposit("ACC-100234", 5000.00, "CUST-01");
            System.out.printf("[TEST 6] Deposit ₹5000: Status=%s, NewBal=₹%.2f (Expected: 53000.00) -> %s\n",
                    d1.responseCode, d1.newBalance, (d1.success && d1.newBalance == 53000.00) ? "PASS" : "FAIL");
            if (d1.success && d1.newBalance == 53000.00) passed++;

            // Test 7: Audit Log and Transaction History Retrieval
            total++;
            List<TransactionRecord> txs = repo.getRecentTransactions(10);
            List<NetworkLogEntry> logs = repo.getRecentLogs(10);
            System.out.printf("[TEST 7] Ledger transactions: %d, Audit logs: %d -> %s\n",
                    txs.size(), logs.size(), (txs.size() >= 3 && !logs.isEmpty()) ? "PASS" : "FAIL");
            if (txs.size() >= 3 && !logs.isEmpty()) passed++;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Clean up test DB
            f.delete();
        }

        System.out.println("================================================================");
        System.out.printf("PHASE 10 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
