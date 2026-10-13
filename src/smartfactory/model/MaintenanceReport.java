package smartfactory.model;

import java.util.Objects;

/**
 * Represents an incident or maintenance log submitted for a machine in the plant.
 * Contains unstructured and structured diagnostic text for string algorithms and indexing.
 */
public class MaintenanceReport {
    private String reportId;
    private String machineId;
    private String technicianId;
    private String logTimestamp;
    private String severity; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    private String faultDescription;
    private String resolutionNotes;

    public MaintenanceReport() {
    }

    public MaintenanceReport(String reportId, String machineId, String technicianId,
                             String logTimestamp, String severity,
                             String faultDescription, String resolutionNotes) {
        this.reportId = reportId;
        this.machineId = machineId;
        this.technicianId = technicianId;
        this.logTimestamp = logTimestamp;
        this.severity = severity;
        this.faultDescription = faultDescription;
        this.resolutionNotes = resolutionNotes;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getLogTimestamp() {
        return logTimestamp;
    }

    public void setLogTimestamp(String logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaintenanceReport that)) return false;
        return Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId);
    }

    @Override
    public String toString() {
        return String.format("MaintenanceReport[ID=%s, Machine=%s, Tech=%s, Severity=%s, Desc='%s']",
                reportId, machineId, technicianId, severity, faultDescription);
    }
}
