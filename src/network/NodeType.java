package network;

/**
 * Enumeration representing different types of network nodes in the banking infrastructure.
 */
public enum NodeType {
    ATM("Automated Teller Machine", "Client terminal for cash withdrawals and balance inquiry"),
    WORKSTATION("Bank Workstation", "Employee terminal for customer service and branch management"),
    ROUTER("Network Router", "Layer 3 packet routing and inter-network switching gateway"),
    SERVER("Enterprise Server", "Application and authorization processing server"),
    DATABASE("Database Server", "Secure central repository for accounts and ledger transactions");

    private final String displayName;
    private final String description;

    NodeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
