package com.college.resource.dto.request;

import com.college.resource.enums.LabStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
public class LabRequest {
    @NotBlank private String labCode;
    @NotBlank private String labName;
    @NotBlank private String location;
    @Min(1)  private int capacity;
    private LabStatus status;
    private String description;


    public LabRequest() {
    }

    public LabRequest(String labCode, String labName, String location, int capacity, LabStatus status, String description) {
        this.labCode = labCode;
        this.labName = labName;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
        this.description = description;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LabStatus getStatus() {
        return status;
    }

    public void setStatus(LabStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
