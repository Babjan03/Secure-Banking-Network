package banking;

/**
 * Fictional banking customer entity for academic demonstration.
 */
public class Customer {

    private final String customerId;
    private final String name;
    private final String accountNumber;
    private final String accountType; // "SAVINGS", "CURRENT"
    private double balance;

    public Customer(String customerId, String name, String accountNumber, String accountType, double balance) {
        this.customerId = customerId;
        this.name = name;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Customer[%s] %s | Acc: %s (%s) | Balance: ₹%.2f",
                customerId, name, accountNumber, accountType, balance);
    }
}
