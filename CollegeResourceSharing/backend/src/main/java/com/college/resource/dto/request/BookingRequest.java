package com.college.resource.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingRequest {
    @NotNull private Long equipmentId;
    @NotBlank private String purpose;
    @NotNull private LocalDate bookingDate;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    @Min(1)  private int quantityBooked = 1;


    public BookingRequest() {
    }

    public BookingRequest(Long equipmentId, String purpose, LocalDate bookingDate, LocalTime startTime, LocalTime endTime, int quantityBooked) {
        this.equipmentId = equipmentId;
        this.purpose = purpose;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quantityBooked = quantityBooked;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getQuantityBooked() {
        return quantityBooked;
    }

    public void setQuantityBooked(int quantityBooked) {
        this.quantityBooked = quantityBooked;
    }

}
