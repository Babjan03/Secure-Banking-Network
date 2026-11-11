package security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Enterprise Banking AES-256-GCM Encryption Service.
 * Implements authenticated encryption with associated data (AEAD) to guarantee
 * confidentiality, integrity, and authenticity for financial transaction payloads.
 */
public class EncryptionService {

    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int AES_KEY_SIZE_BITS = 256;
    public static final int GCM_IV_LENGTH_BYTES = 12; // 96-bit nonce recommended by NIST SP 800-38D
    public static final int GCM_TAG_LENGTH_BITS = 128; // 128-bit authentication tag

    private final SecureRandom secureRandom;

    public EncryptionService() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a new cryptographically secure 256-bit AES master key.
     */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(AES_KEY_SIZE_BITS, new SecureRandom());
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    /**
     * Reconstructs a SecretKey from raw 32-byte key material.
     */
    public static SecretKey createKeyFromBytes(byte[] keyBytes) {
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES-256 key must be exactly 32 bytes (256 bits).");
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * Encrypts plaintext data using AES-256 in GCM mode with a freshly generated random 96-bit nonce.
     */
    public EncryptedPacket encrypt(String plaintext, SecretKey key) throws Exception {
        if (plaintext == null) {
            throw new IllegalArgumentException("Plaintext cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("Secret key cannot be null");
        }

        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertextWithTag = cipher.doFinal(plaintextBytes);

        return new EncryptedPacket(iv, ciphertextWithTag);
    }
}
