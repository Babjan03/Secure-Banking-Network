package banking;

import communication.ATMClient;
import communication.BankingServer;
import communication.TransactionMessage;
import communication.TransactionResponse;
import database.BankingRepository;
import database.DatabaseConnection;
import database.DatabaseInitializer;
import network.NetworkArchitecture;
import network.NetworkNode;
import network.NetworkTopology;
import routing.Dijkstra;
import routing.DistanceVectorSimulation;
import routing.RouteResult;
import security.EncryptionService;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Central orchestrator connecting all banking networking, routing,
 * cryptographic, and database subsystems into a unified service.
 */
public class BankingService {

    public static final int DEFAULT_SERVER_PORT = 9090;

    private final NetworkTopology topology;
    private final DatabaseConnection dbConn;
    private final BankingRepository repository;
    private final SecretKey masterKey;
    private final BankingServer bankingServer;
    private final DistanceVectorSimulation dvSimulation;

    public BankingService() {
        this(DEFAULT_SERVER_PORT);
    }

    public BankingService(int serverPort) {
        this.topology = new NetworkTopology();
        this.dbConn = new DatabaseConnection();
        this.repository = new BankingRepository(dbConn);
        this.masterKey = EncryptionService.generateKey();

        // Initialize Database
        try {
            DatabaseInitializer initializer = new DatabaseInitializer(dbConn);
            initializer.initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }

        // Initialize Distance Vector simulation
        this.dvSimulation = new DistanceVectorSimulation(topology);

        // Initialize Banking Server and bind request processing to database
        this.bankingServer = new BankingServer(serverPort, masterKey);
        this.bankingServer.setRequestProcessor(this::handleIncomingTransaction);
    }

    public void startServer() throws IOException {
        if (!bankingServer.isRunning()) {
            bankingServer.start();
            repository.logNetworkEvent("SYS", "SRV-HQ", "TCP", "Banking Server daemon initialized on port " + bankingServer.getPort());
        }
    }

    public void stopServer() {
        if (bankingServer.isRunning()) {
            bankingServer.stop();
            repository.logNetworkEvent("SYS", "SRV-HQ", "TCP", "Banking Server stopped.");
        }
    }

    /**
     * Central transaction handler executed by the Banking Server for each incoming TCP request.
     */
    private TransactionResponse handleIncomingTransaction(TransactionMessage req) {
        String srcNode = req.getSourceNodeId();
        String acc = req.getAccountNumber();
        double amt = req.getAmount();

        repository.logNetworkEvent(srcNode, "SRV-HQ", "AES-GCM",
                String.format("Server decrypted transaction request %s for account %s", req.getRequestId(), acc));

        BankingRepository.TransactionResult dbRes;
        if (req.getType() == TransactionMessage.Type.WITHDRAW) {
            dbRes = repository.processWithdrawal(acc, amt, srcNode);
        } else if (req.getType() == TransactionMessage.Type.DEPOSIT) {
            dbRes = repository.processDeposit(acc, amt, srcNode);
        } else {
            // Balance Inquiry
            try {
                Double bal = repository.getBalance(acc);
                if (bal == null) {
                    dbRes = new BankingRepository.TransactionResult(false, "ERR_NO_ACCOUNT", "Account not found", 0, req.getRequestId());
                } else {
                    dbRes = new BankingRepository.TransactionResult(true, "OK_200", "Balance verified", bal, req.getRequestId());
                }
            } catch (SQLException e) {
                dbRes = new BankingRepository.TransactionResult(false, "ERR_DB", e.getMessage(), 0, req.getRequestId());
            }
        }

        return new TransactionResponse(
                req.getRequestId(),
                dbRes.success,
                dbRes.responseCode,
                dbRes.message,
                dbRes.newBalance
        );
    }

    /**
     * Executes an end-to-end transaction from an ATM client over the simulated network.
     * Computes the dynamic routing path, ensures the route is alive, and transmits over TCP.
     */
    public EndToEndResult executeAtmTransaction(String atmNodeId, String accountNumber, TransactionMessage.Type type, double amount) throws Exception {
        long startTime = System.currentTimeMillis();

        // 1. Calculate routing path using Dijkstra
        RouteResult route = Dijkstra.findShortestPath(topology, atmNodeId, "SRV-HQ");
        if (!route.isReachable()) {
            repository.logNetworkEvent(atmNodeId, "SRV-HQ", "ROUTING", "Transaction aborted: Destination unreachable due to link failure!");
            return new EndToEndResult(false, route, null, "Network Path Unreachable (Routers Disconnected)", 0);
        }

        repository.logNetworkEvent(atmNodeId, "SRV-HQ", "DIJKSTRA",
                "Routing resolved via path: " + route.getFormattedPath() + " (Total Cost: " + route.getTotalCost() + ")");

        // 2. Ensure server is active
        if (!bankingServer.isRunning()) {
            startServer();
        }

        // 3. Create client and transmit over real TCP socket
        ATMClient client = new ATMClient(atmNodeId, "127.0.0.1", bankingServer.getPort(), masterKey);
        TransactionResponse response = client.sendTransaction(accountNumber, type, amount);

        long duration = System.currentTimeMillis() - startTime;
        return new EndToEndResult(response.isSuccess(), route, response, response.getMessage(), duration);
    }

    public static class EndToEndResult {
        public final boolean success;
        public final RouteResult route;
        public final TransactionResponse response;
        public final String statusMessage;
        public final long durationMs;

        public EndToEndResult(boolean success, RouteResult route, TransactionResponse response, String statusMessage, long durationMs) {
            this.success = success;
            this.route = route;
            this.response = response;
            this.statusMessage = statusMessage;
            this.durationMs = durationMs;
        }
    }

    public NetworkTopology getTopology() {
        return topology;
    }

    public BankingRepository getRepository() {
        return repository;
    }

    public BankingServer getBankingServer() {
        return bankingServer;
    }

    public DistanceVectorSimulation getDvSimulation() {
        return dvSimulation;
    }

    public SecretKey getMasterKey() {
        return masterKey;
    }
}
