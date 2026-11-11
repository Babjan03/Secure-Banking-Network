package network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a discrete physical or virtual device in the banking communication network.
 */
public class NetworkNode {

    private final String id;
    private final String name;
    private final NodeType type;
    private final String macAddress;
    private IPAddress primaryIp;
    private String subnetCidr;
    private IPAddress defaultGateway;
    private boolean isOnline;
    private final List<IPAddress> additionalInterfaces;

    public NetworkNode(String id, String name, NodeType type, IPAddress primaryIp, String subnetCidr, IPAddress defaultGateway, String macAddress) {
        this.id = Objects.requireNonNull(id, "Node ID cannot be null");
        this.name = Objects.requireNonNull(name, "Node name cannot be null");
        this.type = Objects.requireNonNull(type, "Node type cannot be null");
        this.primaryIp = Objects.requireNonNull(primaryIp, "Primary IP cannot be null");
        this.subnetCidr = subnetCidr;
        this.defaultGateway = defaultGateway;
        this.macAddress = macAddress;
        this.isOnline = true;
        this.additionalInterfaces = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public IPAddress getPrimaryIp() {
        return primaryIp;
    }

    public void setPrimaryIp(IPAddress primaryIp) {
        this.primaryIp = primaryIp;
    }

    public String getSubnetCidr() {
        return subnetCidr;
    }

    public void setSubnetCidr(String subnetCidr) {
        this.subnetCidr = subnetCidr;
    }

    public IPAddress getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(IPAddress defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void addInterface(IPAddress ip) {
        if (ip != null && !additionalInterfaces.contains(ip)) {
            additionalInterfaces.add(ip);
        }
    }

    public List<IPAddress> getAllInterfaces() {
        List<IPAddress> all = new ArrayList<>();
        all.add(primaryIp);
        all.addAll(additionalInterfaces);
        return Collections.unmodifiableList(all);
    }

    public boolean hasIp(IPAddress ip) {
        if (primaryIp.equals(ip)) return true;
        for (IPAddress iface : additionalInterfaces) {
            if (iface.equals(ip)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - IP: %s [Status: %s]",
                id, name, type.getDisplayName(), primaryIp, isOnline ? "ONLINE" : "OFFLINE");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkNode that = (NetworkNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
