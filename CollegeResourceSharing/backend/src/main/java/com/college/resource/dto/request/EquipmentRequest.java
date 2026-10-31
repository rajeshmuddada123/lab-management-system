package com.college.resource.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class EquipmentRequest {
    @NotNull private Long labId;
    @NotBlank private String equipmentCode;
    @NotBlank private String name;
    private String description;
    @NotBlank private String category;
    private String makeModel;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    @Min(0) private int quantityTotal;
    @Min(0) private int quantityAvailable;
    private String conditionStatus;


    public EquipmentRequest() {
    }

    public EquipmentRequest(Long labId, String equipmentCode, String name, String description, String category, String makeModel, String serialNumber, LocalDate purchaseDate, LocalDate warrantyExpiry, int quantityTotal, int quantityAvailable, String conditionStatus) {
        this.labId = labId;
        this.equipmentCode = equipmentCode;
        this.name = name;
        this.description = description;
        this.category = category;
        this.makeModel = makeModel;
        this.serialNumber = serialNumber;
        this.purchaseDate = purchaseDate;
        this.warrantyExpiry = warrantyExpiry;
        this.quantityTotal = quantityTotal;
        this.quantityAvailable = quantityAvailable;
        this.conditionStatus = conditionStatus;
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel(String makeModel) {
        this.makeModel = makeModel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getWarrantyExpiry() {
        return warrantyExpiry;
    }

    public void setWarrantyExpiry(LocalDate warrantyExpiry) {
        this.warrantyExpiry = warrantyExpiry;
    }

    public int getQuantityTotal() {
        return quantityTotal;
    }

    public void setQuantityTotal(int quantityTotal) {
        this.quantityTotal = quantityTotal;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public String getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

}
