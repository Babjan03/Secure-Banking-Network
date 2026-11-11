package communication;

import security.DecryptionService;
import security.EncryptedPacket;
import security.EncryptionService;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Real Java TCP Client representing an ATM terminal (e.g. ATM-01).
 * Connects to the Banking Server, encrypts financial payloads using AES-256-GCM,
 * transmits over streaming TCP with length-prefixed framing, and awaits response.
 */
public class ATMClient {

    private final String clientNodeId;
    private final String serverHost;
    private final int serverPort;
    private final SecretKey masterKey;
    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;

    public ATMClient(String clientNodeId, String serverHost, int serverPort, SecretKey masterKey) {
        this.clientNodeId = clientNodeId;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.masterKey = masterKey;
        this.encryptionService = new EncryptionService();
        this.decryptionService = new DecryptionService();
    }

    /**
     * Executes a full transactional request over TCP with authenticated AES-GCM encryption.
     */
    public TransactionResponse sendTransaction(String accountNumber, TransactionMessage.Type type, double amount) throws Exception {
        TransactionMessage request = new TransactionMessage(clientNodeId, accountNumber, type, amount);
        String plainRequest = request.serialize();

        // 1. Encrypt transaction payload
        EncryptedPacket encPacket = encryptionService.encrypt(plainRequest, masterKey);
        String wirePayload = encPacket.toWireString();

        // 2. Open TCP connection to Server
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(serverHost, serverPort), 5000); // 5 sec connect timeout
            socket.setSoTimeout(7000); // 7 sec read timeout

            // 3. Send framed encrypted payload
            MessageProtocol.sendFramedMessage(socket.getOutputStream(), wirePayload);

            // 4. Receive framed response
            String responseWire = MessageProtocol.receiveFramedMessage(socket.getInputStream());
            if (responseWire == null) {
                throw new IOException("Server closed connection without responding.");
            }

            // 5. Decrypt server response
            EncryptedPacket respPacket = EncryptedPacket.fromWireString(responseWire);
            String plainResponse = decryptionService.decrypt(respPacket, masterKey);

            // 6. Deserialize and return
            return TransactionResponse.deserialize(plainResponse);
        }
    }

    public String getClientNodeId() {
        return clientNodeId;
    }
}
