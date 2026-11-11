package network;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents an IPv4 address with full 32-bit binary representation,
 * octet-level validation, address classification, and conversion utilities.
 */
public class IPAddress implements Comparable<IPAddress> {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    private final long addressValue; // 32-bit unsigned representation stored in a 64-bit long
    private final String dottedDecimal;

    /**
     * Constructs an IPAddress from a standard dotted-decimal string (e.g. "192.168.1.1").
     * @param ipString Dotted-decimal IPv4 address string
     * @throws IllegalArgumentException if the format is invalid or octets are out of range [0, 255]
     */
    public IPAddress(String ipString) {
        if (ipString == null || !isValid(ipString.trim())) {
            throw new IllegalArgumentException("Invalid IPv4 address format: '" + ipString + "'. Must be 4 octets between 0 and 255.");
        }
        String clean = ipString.trim();
        String[] parts = clean.split("\\.");
        long val = 0;
        for (int i = 0; i < 4; i++) {
            long octet = Long.parseLong(parts[i]);
            val = (val << 8) | octet;
        }
        this.addressValue = val;
        this.dottedDecimal = clean;
    }

    /**
     * Constructs an IPAddress from a 32-bit integer value.
     * @param addressValue 32-bit unsigned integer value
     */
    public IPAddress(long addressValue) {
        this.addressValue = addressValue & 0xFFFFFFFFL;
        this.dottedDecimal = String.format("%d.%d.%d.%d",
            (this.addressValue >> 24) & 0xFF,
            (this.addressValue >> 16) & 0xFF,
            (this.addressValue >> 8) & 0xFF,
            this.addressValue & 0xFF
        );
    }

    /**
     * Validates if a string is a strictly compliant IPv4 dotted-decimal address.
     */
    public static boolean isValid(String ip) {
        if (ip == null) return false;
        String trimmed = ip.trim();
        if (!IPV4_PATTERN.matcher(trimmed).matches()) {
            return false;
        }
        String[] parts = trimmed.split("\\.");
        if (parts.length != 4) return false;
        for (String p : parts) {
            try {
                int octet = Integer.parseInt(p);
                if (octet < 0 || octet > 255) return false;
                // Disallow leading zeros unless the octet is single zero (e.g., "01" is invalid)
                if (p.length() > 1 && p.startsWith("0")) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the 32-bit numerical value of the address.
     */
    public long getAddressValue() {
        return addressValue;
    }

    /**
     * Gets the individual octet (0 to 3, where 0 is the most significant octet).
     */
    public int getOctet(int index) {
        if (index < 0 || index > 3) {
            throw new IndexOutOfBoundsException("Octet index must be between 0 and 3.");
        }
        int shift = (3 - index) * 8;
        return (int) ((addressValue >> shift) & 0xFF);
    }

    /**
     * Returns the 32-bit binary string representation formatted as 4 octets separated by dots.
     * Example: "11000000.10101000.00000001.00000001"
     */
    public String toBinaryString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int octet = getOctet(i);
            String bin = String.format("%8s", Integer.toBinaryString(octet)).replace(' ', '0');
            sb.append(bin);
            if (i < 3) sb.append(".");
        }
        return sb.toString();
    }

    /**
     * Determines the legacy IPv4 class of the address (Class A, B, C, D, or E).
     */
    public String getAddressClass() {
        int firstOctet = getOctet(0);
        if (firstOctet >= 1 && firstOctet <= 126) return "Class A (Unicast - Large Networks)";
        if (firstOctet == 127) return "Class A (Loopback Network)";
        if (firstOctet >= 128 && firstOctet <= 191) return "Class B (Unicast - Medium Networks)";
        if (firstOctet >= 192 && firstOctet <= 223) return "Class C (Unicast - Small/LAN Networks)";
        if (firstOctet >= 224 && firstOctet <= 239) return "Class D (Multicast Group)";
        return "Class E (Experimental / Reserved)";
    }

    /**
     * Checks if this address belongs to RFC 1918 private address ranges.
     */
    public boolean isPrivateAddress() {
        int o0 = getOctet(0);
        int o1 = getOctet(1);
        // 10.0.0.0 - 10.255.255.255 (/8)
        if (o0 == 10) return true;
        // 172.16.0.0 - 172.31.255.255 (/12)
        if (o0 == 172 && (o1 >= 16 && o1 <= 31)) return true;
        // 192.168.0.0 - 192.168.255.255 (/16)
        if (o0 == 192 && o1 == 168) return true;
        return false;
    }

    /**
     * Checks if this is a loopback address (127.0.0.0/8).
     */
    public boolean isLoopback() {
        return getOctet(0) == 127;
    }

    @Override
    public String toString() {
        return dottedDecimal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPAddress ipAddress = (IPAddress) o;
        return addressValue == ipAddress.addressValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressValue);
    }

    @Override
    public int compareTo(IPAddress o) {
        return Long.compare(this.addressValue, o.addressValue);
    }
}
