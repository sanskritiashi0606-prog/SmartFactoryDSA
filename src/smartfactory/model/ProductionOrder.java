package smartfactory.model;

import java.util.Objects;

/**
 * Represents an industrial production order scheduled or processed in the factory.
 * Contains scheduling constraints, required machine specifications, and cost estimations.
 */
public class ProductionOrder {
    private String orderId;
    private String productCode;
    private int quantity;
    private int priority;                 // 1 (lowest) to 10 (highest emergency)
    private long dueDateEpochMs;
    private String requiredMachineType;
    private double estimatedCost;

    public ProductionOrder() {
    }

    public ProductionOrder(String orderId, String productCode, int quantity, int priority,
                           long dueDateEpochMs, String requiredMachineType, double estimatedCost) {
        this.orderId = orderId;
        this.productCode = productCode;
        this.quantity = quantity;
        this.priority = priority;
        this.dueDateEpochMs = dueDateEpochMs;
        this.requiredMachineType = requiredMachineType;
        this.estimatedCost = estimatedCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDueDateEpochMs() {
        return dueDateEpochMs;
    }

    public void setDueDateEpochMs(long dueDateEpochMs) {
        this.dueDateEpochMs = dueDateEpochMs;
    }

    public String getRequiredMachineType() {
        return requiredMachineType;
    }

    public void setRequiredMachineType(String requiredMachineType) {
        this.requiredMachineType = requiredMachineType;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductionOrder that)) return false;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return String.format("ProductionOrder[ID=%s, Code='%s', Qty=%d, Pri=%d, Type='%s', Cost=$%.2f]",
                orderId, productCode, quantity, priority, requiredMachineType, estimatedCost);
    }
}
