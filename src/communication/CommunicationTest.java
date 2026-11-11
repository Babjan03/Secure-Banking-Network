package communication;

import security.EncryptionService;

import javax.crypto.SecretKey;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 7 TEST: REAL TCP CLIENT-SERVER COMMUNICATION           ");
        System.out.println("================================================================");

        int passed = 0;
        int total = 0;
        int serverPort = 9095;
        SecretKey masterKey = EncryptionService.generateKey();
        BankingServer server = new BankingServer(serverPort, masterKey);
        AtomicInteger serverPacketsReceived = new AtomicInteger(0);

        server.addListener(new BankingServer.ServerEventListener() {
            @Override public void onServerStarted(int port) {
                System.out.println("   [SERVER EVENT] Banking Server listening on TCP port " + port);
            }
            @Override public void onClientConnected(String clientAddress) {
                System.out.println("   [SERVER EVENT] Accepted connection from: " + clientAddress);
            }
            @Override public void onMessageReceived(String rawWireMessage, String decryptedMessage) {
                serverPacketsReceived.incrementAndGet();
                System.out.println("   [SERVER EVENT] Received Wire: " + rawWireMessage.substring(0, Math.min(40, rawWireMessage.length())) + "...");
                System.out.println("   [SERVER EVENT] Decrypted  : " + decryptedMessage);
            }
            @Override public void onResponseSent(String plainResponse, String encryptedWireResponse) {
                System.out.println("   [SERVER EVENT] Dispatched Encrypted Response back to client.");
            }
            @Override public void onError(String error) {
                System.err.println("   [SERVER ERROR] " + error);
            }
            @Override public void onServerStopped() {
                System.out.println("   [SERVER EVENT] Banking Server stopped.");
            }
        });

        try {
            // Test 1: Start Server
            total++;
            server.start();
            Thread.sleep(300); // Allow server thread to bind socket
            boolean isRunning = server.isRunning();
            System.out.printf("[TEST 1] Server Startup on port %d -> %s\n", serverPort, isRunning ? "PASS" : "FAIL");
            if (isRunning) passed++;

            // Test 2: ATM-01 Withdrawal Transaction
            total++;
            ATMClient atm1 = new ATMClient("ATM-01", "127.0.0.1", serverPort, masterKey);
            TransactionResponse resp1 = atm1.sendTransaction("ACC-100234", TransactionMessage.Type.WITHDRAW, 2000.0);
            System.out.println("[TEST 2] ATM-01 Response: " + resp1);
            boolean t2Ok = resp1.isSuccess() && resp1.getRemainingBalance() == 48000.0;
            System.out.printf("         Withdrawal Result: %s\n", t2Ok ? "PASS" : "FAIL");
            if (t2Ok) passed++;

            // Test 3: ATM-02 Deposit Transaction
            total++;
            ATMClient atm2 = new ATMClient("ATM-02", "127.0.0.1", serverPort, masterKey);
            TransactionResponse resp2 = atm2.sendTransaction("ACC-500891", TransactionMessage.Type.DEPOSIT, 5000.0);
            System.out.println("[TEST 3] ATM-02 Response: " + resp2);
            boolean t3Ok = resp2.isSuccess() && resp2.getRemainingBalance() == 55000.0;
            System.out.printf("         Deposit Result   : %s\n", t3Ok ? "PASS" : "FAIL");
            if (t3Ok) passed++;

            // Test 4: Packet Count Verification
            total++;
            boolean t4Ok = serverPacketsReceived.get() == 2;
            System.out.printf("[TEST 4] Server Packets Processed: %d (Expected: 2) -> %s\n", serverPacketsReceived.get(), t4Ok ? "PASS" : "FAIL");
            if (t4Ok) passed++;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }

        System.out.println("================================================================");
        System.out.printf("PHASE 7 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
