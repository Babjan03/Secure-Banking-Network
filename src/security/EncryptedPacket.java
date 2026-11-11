package security;

import java.util.Base64;
import java.util.Objects;

/**
 * Encapsulates a cryptographic transmission packet containing the AES-GCM Initialization Vector (IV/Nonce),
 * the ciphertext with the embedded 128-bit Galois/Counter Mode authentication tag, and metadata.
 */
public class EncryptedPacket {

    private final byte[] iv;
    private final byte[] ciphertextWithTag;
    private final long timestamp;

    public EncryptedPacket(byte[] iv, byte[] ciphertextWithTag) {
        this.iv = Objects.requireNonNull(iv, "IV cannot be null");
        this.ciphertextWithTag = Objects.requireNonNull(ciphertextWithTag, "Ciphertext cannot be null");
        this.timestamp = System.currentTimeMillis();
    }

    public byte[] getIv() {
        return iv.clone();
    }

    public byte[] getCiphertextWithTag() {
        return ciphertextWithTag.clone();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getIvBase64() {
        return Base64.getEncoder().encodeToString(iv);
    }

    public String getCiphertextBase64() {
        return Base64.getEncoder().encodeToString(ciphertextWithTag);
    }

    /**
     * Serializes to a standard delimited transmission payload format:
     * "ENC:<Base64(IV)>:<Base64(Ciphertext+Tag)>"
     */
    public String toWireString() {
        return "ENC:" + getIvBase64() + ":" + getCiphertextBase64();
    }

    /**
     * Deserializes from a wire string.
     */
    public static EncryptedPacket fromWireString(String wire) {
        if (wire == null || !wire.startsWith("ENC:")) {
            throw new IllegalArgumentException("Malformed encrypted packet format.");
        }
        String[] parts = wire.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted packet segments.");
        }
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] cipher = Base64.getDecoder().decode(parts[2]);
        return new EncryptedPacket(iv, cipher);
    }

    /**
     * Creates a mutated copy of this packet with a flipped bit in the ciphertext
     * to simulate in-transit transmission tampering or active MITM manipulation.
     */
    public EncryptedPacket createTamperedCopy() {
        byte[] tamperedCipher = ciphertextWithTag.clone();
        if (tamperedCipher.length > 0) {
            tamperedCipher[0] ^= 0x01; // Flip the least significant bit of first byte
        }
        return new EncryptedPacket(this.iv.clone(), tamperedCipher);
    }

    @Override
    public String toString() {
        return String.format("EncryptedPacket [IV: %s, Ciphertext Len: %d bytes]",
                getIvBase64(), ciphertextWithTag.length);
    }
}
