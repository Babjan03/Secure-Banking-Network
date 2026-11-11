package network;

/**
 * Provides comprehensive IPv4 subnet calculations:
 * CIDR conversions, subnet masks, network address, broadcast address,
 * usable host ranges, and subnet membership verification.
 */
public class SubnetCalculator {

    public static class SubnetDetails {
        public final IPAddress ipAddress;
        public final int prefixLength;
        public final IPAddress subnetMask;
        public final IPAddress networkAddress;
        public final IPAddress broadcastAddress;
        public final IPAddress firstUsableIp;
        public final IPAddress lastUsableIp;
        public final long totalHosts;
        public final long usableHosts;
        public final String wildcardMask;

        public SubnetDetails(IPAddress ipAddress, int prefixLength, IPAddress subnetMask,
                             IPAddress networkAddress, IPAddress broadcastAddress,
                             IPAddress firstUsableIp, IPAddress lastUsableIp,
                             long totalHosts, long usableHosts, String wildcardMask) {
            this.ipAddress = ipAddress;
            this.prefixLength = prefixLength;
            this.subnetMask = subnetMask;
            this.networkAddress = networkAddress;
            this.broadcastAddress = broadcastAddress;
            this.firstUsableIp = firstUsableIp;
            this.lastUsableIp = lastUsableIp;
            this.totalHosts = totalHosts;
            this.usableHosts = usableHosts;
            this.wildcardMask = wildcardMask;
        }

        @Override
        public String toString() {
            return String.format(
                "Subnet Details for %s/%d:\n" +
                "  Subnet Mask      : %s\n" +
                "  Wildcard Mask    : %s\n" +
                "  Network Address  : %s\n" +
                "  Broadcast Address: %s\n" +
                "  Usable IP Range  : %s - %s\n" +
                "  Usable Hosts     : %,d (Total: %,d)",
                ipAddress, prefixLength, subnetMask, wildcardMask,
                networkAddress, broadcastAddress,
                firstUsableIp, lastUsableIp,
                usableHosts, totalHosts
            );
        }
    }

    /**
     * Converts a CIDR prefix length (e.g. 24) to a 32-bit subnet mask long.
     */
    public static long prefixToMaskValue(int prefix) {
        if (prefix < 0 || prefix > 32) {
            throw new IllegalArgumentException("Prefix length must be between 0 and 32, got: " + prefix);
        }
        if (prefix == 0) return 0L;
        return (0xFFFFFFFF00000000L >> prefix) & 0xFFFFFFFFL;
    }

    /**
     * Converts a dotted-decimal subnet mask (e.g. "255.255.255.0") to a CIDR prefix length.
     */
    public static int maskToPrefixLength(String maskStr) {
        IPAddress mask = new IPAddress(maskStr);
        long val = mask.getAddressValue();
        // Check for contiguous leading ones
        int count = 0;
        boolean foundZero = false;
        for (int i = 31; i >= 0; i--) {
            long bit = (val >> i) & 1L;
            if (bit == 1L) {
                if (foundZero) {
                    throw new IllegalArgumentException("Non-contiguous subnet mask: " + maskStr);
                }
                count++;
            } else {
                foundZero = true;
            }
        }
        return count;
    }

    /**
     * Calculates complete subnet details for an IP string and prefix.
     */
    public static SubnetDetails calculate(String ipStr, int prefix) {
        IPAddress ip = new IPAddress(ipStr);
        if (prefix < 0 || prefix > 32) {
            throw new IllegalArgumentException("CIDR prefix must be between 0 and 32.");
        }

        long maskVal = prefixToMaskValue(prefix);
        IPAddress subnetMask = new IPAddress(maskVal);

        long netVal = ip.getAddressValue() & maskVal;
        IPAddress networkAddress = new IPAddress(netVal);

        long invMaskVal = (~maskVal) & 0xFFFFFFFFL;
        long bcastVal = netVal | invMaskVal;
        IPAddress broadcastAddress = new IPAddress(bcastVal);

        long totalHosts = (prefix == 32) ? 1 : (1L << (32 - prefix));
        long usableHosts;
        IPAddress firstUsable;
        IPAddress lastUsable;

        if (prefix >= 31) {
            usableHosts = (prefix == 31) ? 2 : 1;
            firstUsable = networkAddress;
            lastUsable = broadcastAddress;
        } else {
            usableHosts = totalHosts - 2;
            firstUsable = new IPAddress(netVal + 1);
            lastUsable = new IPAddress(bcastVal - 1);
        }

        IPAddress wildcard = new IPAddress(invMaskVal);

        return new SubnetDetails(ip, prefix, subnetMask, networkAddress, broadcastAddress,
                firstUsable, lastUsable, totalHosts, usableHosts, wildcard.toString());
    }

    /**
     * Parses a CIDR representation like "192.168.1.10/24".
     */
    public static SubnetDetails calculateCIDR(String cidrNotation) {
        if (cidrNotation == null || !cidrNotation.contains("/")) {
            throw new IllegalArgumentException("CIDR notation must contain '/' (e.g. 192.168.1.10/24)");
        }
        String[] parts = cidrNotation.trim().split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid CIDR format: " + cidrNotation);
        }
        int prefix = Integer.parseInt(parts[1].trim());
        return calculate(parts[0].trim(), prefix);
    }

    /**
     * Checks if two IP addresses reside on the exact same subnet under a given prefix.
     */
    public static boolean isSameSubnet(IPAddress ip1, IPAddress ip2, int prefix) {
        long mask = prefixToMaskValue(prefix);
        return (ip1.getAddressValue() & mask) == (ip2.getAddressValue() & mask);
    }

    /**
     * Checks if two IP addresses can communicate directly at Layer 2 without routing.
     */
    public static boolean canCommunicateDirectly(IPAddress ip1, IPAddress mask1, IPAddress ip2, IPAddress mask2) {
        if (!mask1.equals(mask2)) {
            // Asymmetric masks usually require gateway routing
            return false;
        }
        long m = mask1.getAddressValue();
        return (ip1.getAddressValue() & m) == (ip2.getAddressValue() & m);
    }
}
