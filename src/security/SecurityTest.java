package security;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;

public class SecurityTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 6 TEST: AES-256-GCM ENCRYPTION & AUTHENTICATION        ");
        System.out.println("================================================================");

        int passed = 0;
        int total = 0;

        try {
            EncryptionService encService = new EncryptionService();
            DecryptionService decService = new DecryptionService();
            SecretKey masterKey = EncryptionService.generateKey();

            String sampleTxPayload = "{\"source\":\"ATM-01\",\"acc\":\"ACC-100234\",\"type\":\"WITHDRAW\",\"amount\":2000.00,\"currency\":\"INR\"}";
            System.out.println("Original Banking Payload: " + sampleTxPayload);

            // Test 1: Successful Encryption and Decryption Round-Trip
            total++;
            EncryptedPacket packet1 = encService.encrypt(sampleTxPayload, masterKey);
            String decrypted = decService.decrypt(packet1, masterKey);
            boolean t1Ok = sampleTxPayload.equals(decrypted);
            System.out.println("[TEST 1] Encrypted IV (Base64)        : " + packet1.getIvBase64());
            System.out.println("         Ciphertext+Tag (Base64)      : " + packet1.getCiphertextBase64());
            System.out.println("         Decrypted Plaintext          : " + decrypted);
            System.out.printf("         Round-Trip Result            : %s\n", t1Ok ? "PASS" : "FAIL");
            if (t1Ok) passed++;

            // Test 2: Nonce Freshness (Two encryptions of same message have different ciphertexts)
            total++;
            EncryptedPacket packet2 = encService.encrypt(sampleTxPayload, masterKey);
            boolean differentIv = !packet1.getIvBase64().equals(packet2.getIvBase64());
            boolean differentCipher = !packet1.getCiphertextBase64().equals(packet2.getCiphertextBase64());
            System.out.printf("[TEST 2] Nonce Uniqueness & Semantic Security (Diff IV: %b, Diff Cipher: %b) -> %s\n",
                    differentIv, differentCipher, (differentIv && differentCipher) ? "PASS" : "FAIL");
            if (differentIv && differentCipher) passed++;

            // Test 3: Wire Serialization and Deserialization
            total++;
            String wireFormat = packet1.toWireString();
            EncryptedPacket deserialized = EncryptedPacket.fromWireString(wireFormat);
            String decFromWire = decService.decrypt(deserialized, masterKey);
            boolean t3Ok = sampleTxPayload.equals(decFromWire);
            System.out.printf("[TEST 3] Wire Serialization Format check -> %s\n", t3Ok ? "PASS" : "FAIL");
            if (t3Ok) passed++;

            // Test 4: Tamper Detection (Integrity Violation via AEADBadTagException)
            total++;
            EncryptedPacket tamperedPacket = packet1.createTamperedCopy();
            boolean caughtTamper = false;
            try {
                decService.decrypt(tamperedPacket, masterKey);
            } catch (AEADBadTagException e) {
                caughtTamper = true;
                System.out.println("   [SECURITY ALERT] Tamper successfully detected by GCM: " + e.getMessage());
            } catch (Exception e) {
                caughtTamper = true;
                System.out.println("   [SECURITY ALERT] Exception caught on tampered packet: " + e.getClass().getSimpleName());
            }
            System.out.printf("[TEST 4] Cryptographic Authentication Tag Rejection -> %s\n", caughtTamper ? "PASS" : "FAIL");
            if (caughtTamper) passed++;

            // Test 5: Key Mismatch Detection
            total++;
            SecretKey attackerKey = EncryptionService.generateKey();
            boolean caughtWrongKey = false;
            try {
                decService.decrypt(packet1, attackerKey);
            } catch (Exception e) {
                caughtWrongKey = true;
                System.out.println("   [SECURITY ALERT] Unauthorized key rejected: " + e.getClass().getSimpleName());
            }
            System.out.printf("[TEST 5] Decryption with Wrong Key Rejected -> %s\n", caughtWrongKey ? "PASS" : "FAIL");
            if (caughtWrongKey) passed++;

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("================================================================");
        System.out.printf("PHASE 6 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
