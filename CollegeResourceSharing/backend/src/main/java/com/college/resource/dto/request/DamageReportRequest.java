package com.college.resource.dto.request;

import com.college.resource.enums.DamageSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DamageReportRequest {
    @NotNull  private Long equipmentId;
    private Long bookingId;
    @NotNull  private DamageSeverity severity;
    @NotBlank private String description;
    private BigDecimal repairCost;


    public DamageReportRequest() {
    }

    public DamageReportRequest(Long equipmentId, Long bookingId, DamageSeverity severity, String description, BigDecimal repairCost) {
        this.equipmentId = equipmentId;
        this.bookingId = bookingId;
        this.severity = severity;
        this.description = description;
        this.repairCost = repairCost;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public DamageSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DamageSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(BigDecimal repairCost) {
        this.repairCost = repairCost;
    }

}
