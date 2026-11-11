package communication;

import security.DecryptionService;
import security.EncryptedPacket;
import security.EncryptionService;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-threaded TCP Banking Server listening on a network socket.
 * Receives framed AES-GCM encrypted requests, decrypts and processes them,
 * and transmits back encrypted transaction responses.
 */
public class BankingServer {

    public interface ServerEventListener {
        void onServerStarted(int port);
        void onClientConnected(String clientAddress);
        void onMessageReceived(String rawWireMessage, String decryptedMessage);
        void onResponseSent(String plainResponse, String encryptedWireResponse);
        void onError(String error);
        void onServerStopped();
    }

    public interface RequestProcessor {
        TransactionResponse process(TransactionMessage request);
    }

    private final int port;
    private final SecretKey masterKey;
    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private final AtomicBoolean running;
    private RequestProcessor requestProcessor;
    private final List<ServerEventListener> listeners;

    public BankingServer(int port, SecretKey masterKey) {
        this.port = port;
        this.masterKey = masterKey;
        this.encryptionService = new EncryptionService();
        this.decryptionService = new DecryptionService();
        this.running = new AtomicBoolean(false);
        this.listeners = new CopyOnWriteArrayList<>();

        // Default mock processor (overridden in full integration with Database)
        this.requestProcessor = req -> new TransactionResponse(
                req.getRequestId(),
                true,
                "OK_200",
                "Transaction approved for account " + req.getAccountNumber(),
                req.getType() == TransactionMessage.Type.WITHDRAW ? 50000.0 - req.getAmount() : 50000.0 + req.getAmount()
        );
    }

    public void setRequestProcessor(RequestProcessor processor) {
        this.requestProcessor = processor;
    }

    public void addListener(ServerEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ServerEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void start() throws IOException {
        if (running.get()) return;

        serverSocket = new ServerSocket(port);
        threadPool = Executors.newCachedThreadPool();
        running.set(true);

        for (ServerEventListener l : listeners) l.onServerStarted(port);

        threadPool.execute(this::acceptLoop);
    }

    private void acceptLoop() {
        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(10000); // 10 second timeout
                String clientAddr = clientSocket.getRemoteSocketAddress().toString();
                for (ServerEventListener l : listeners) l.onClientConnected(clientAddr);

                threadPool.execute(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (!running.get()) break;
                for (ServerEventListener l : listeners) l.onError("Accept loop error: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket socket) {
        try (socket) {
            String wireMessage = MessageProtocol.receiveFramedMessage(socket.getInputStream());
            if (wireMessage == null) return;

            // 1. Decrypt incoming wire packet
            EncryptedPacket encPacket = EncryptedPacket.fromWireString(wireMessage);
            String decryptedPayload = decryptionService.decrypt(encPacket, masterKey);

            for (ServerEventListener l : listeners) {
                l.onMessageReceived(wireMessage, decryptedPayload);
            }

            // 2. Deserialize transaction request
            TransactionMessage req = TransactionMessage.deserialize(decryptedPayload);

            // 3. Process business logic (database balance checks / transaction updates)
            TransactionResponse resp = requestProcessor.process(req);
            String plainResponse = resp.serialize();

            // 4. Encrypt response with fresh Nonce
            EncryptedPacket respPacket = encryptionService.encrypt(plainResponse, masterKey);
            String respWire = respPacket.toWireString();

            // 5. Send framed response back
            MessageProtocol.sendFramedMessage(socket.getOutputStream(), respWire);

            for (ServerEventListener l : listeners) {
                l.onResponseSent(plainResponse, respWire);
            }
        } catch (Exception e) {
            for (ServerEventListener l : listeners) {
                l.onError("Client handler error: " + e.getMessage());
            }
        }
    }

    public synchronized void stop() {
        if (!running.get()) return;
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        for (ServerEventListener l : listeners) l.onServerStopped();
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getPort() {
        return port;
    }
}
