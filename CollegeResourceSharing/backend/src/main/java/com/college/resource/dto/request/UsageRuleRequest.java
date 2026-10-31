package com.college.resource.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class UsageRuleRequest {
    @NotNull private Long labId;
    @Min(1)  private int maxBookingDurationHours = 4;
    @Min(1)  private int maxAdvanceBookingDays   = 7;
    @Min(1)  private int maxConcurrentBookingsPerStudent = 1;
    @NotBlank private String allowedDays;
    @NotNull  private LocalTime openTime;
    @NotNull  private LocalTime closeTime;
    private String notes;


    public UsageRuleRequest() {
    }

    public UsageRuleRequest(Long labId, int maxBookingDurationHours, int maxAdvanceBookingDays, int maxConcurrentBookingsPerStudent, String allowedDays, LocalTime openTime, LocalTime closeTime, String notes) {
        this.labId = labId;
        this.maxBookingDurationHours = maxBookingDurationHours;
        this.maxAdvanceBookingDays = maxAdvanceBookingDays;
        this.maxConcurrentBookingsPerStudent = maxConcurrentBookingsPerStudent;
        this.allowedDays = allowedDays;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.notes = notes;
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public int getMaxBookingDurationHours() {
        return maxBookingDurationHours;
    }

    public void setMaxBookingDurationHours(int maxBookingDurationHours) {
        this.maxBookingDurationHours = maxBookingDurationHours;
    }

    public int getMaxAdvanceBookingDays() {
        return maxAdvanceBookingDays;
    }

    public void setMaxAdvanceBookingDays(int maxAdvanceBookingDays) {
        this.maxAdvanceBookingDays = maxAdvanceBookingDays;
    }

    public int getMaxConcurrentBookingsPerStudent() {
        return maxConcurrentBookingsPerStudent;
    }

    public void setMaxConcurrentBookingsPerStudent(int maxConcurrentBookingsPerStudent) {
        this.maxConcurrentBookingsPerStudent = maxConcurrentBookingsPerStudent;
    }

    public String getAllowedDays() {
        return allowedDays;
    }

    public void setAllowedDays(String allowedDays) {
        this.allowedDays = allowedDays;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
