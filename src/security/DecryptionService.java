package security;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;

/**
 * Enterprise Banking AES-256-GCM Decryption and Authentication Service.
 * Decrypts payloads and cryptographically verifies the 128-bit authentication tag.
 * Rejects modified ciphertexts or tampered IVs by raising AEADBadTagException.
 */
public class DecryptionService {

    /**
     * Decrypts an EncryptedPacket using the designated banking SecretKey.
     * @return Decrypted UTF-8 plaintext string
     * @throws AEADBadTagException if the ciphertext or authentication tag has been tampered with
     * @throws Exception on key mismatch or cryptographic failure
     */
    public String decrypt(EncryptedPacket packet, SecretKey key) throws Exception {
        if (packet == null || key == null) {
            throw new IllegalArgumentException("Packet and key cannot be null");
        }

        Cipher cipher = Cipher.getInstance(EncryptionService.TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(EncryptionService.GCM_TAG_LENGTH_BITS, packet.getIv());
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] decryptedBytes = cipher.doFinal(packet.getCiphertextWithTag());
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
