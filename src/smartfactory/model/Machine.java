package smartfactory.model;

import java.util.Objects;

/**
 * Represents an industrial manufacturing machine within the SmartFactory platform.
 * Encapsulates operational capacity, machine category, and real-time status.
 */
public class Machine {
    private String id;
    private String name;
    private String type;
    private int capacityUnitsPerHour;
    private String operationalStatus; // "OPERATIONAL", "MAINTENANCE_REQUIRED", "IDLE", "OFFLINE"
    private double failureRiskScore;   // 0.0 to 1.0

    public Machine() {
    }

    public Machine(String id, String name, String type, int capacityUnitsPerHour, String operationalStatus, double failureRiskScore) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacityUnitsPerHour = capacityUnitsPerHour;
        this.operationalStatus = operationalStatus;
        this.failureRiskScore = failureRiskScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacityUnitsPerHour() {
        return capacityUnitsPerHour;
    }

    public void setCapacityUnitsPerHour(int capacityUnitsPerHour) {
        this.capacityUnitsPerHour = capacityUnitsPerHour;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public double getFailureRiskScore() {
        return failureRiskScore;
    }

    public void setFailureRiskScore(double failureRiskScore) {
        this.failureRiskScore = failureRiskScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Machine machine)) return false;
        return Objects.equals(id, machine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Machine[ID=%s, Name='%s', Type='%s', Cap=%d/hr, Status=%s, Risk=%.2f]",
                id, name, type, capacityUnitsPerHour, operationalStatus, failureRiskScore);
    }
}
