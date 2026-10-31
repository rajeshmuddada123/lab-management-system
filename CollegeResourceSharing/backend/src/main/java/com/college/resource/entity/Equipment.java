package com.college.resource.entity;

import com.college.resource.enums.EquipmentCondition;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long equipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", nullable = false)
    private Laboratory laboratory;

    @Column(name = "equipment_code", nullable = false, unique = true, length = 30)
    private String equipmentCode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "make_model", length = 200)
    private String makeModel;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @Column(name = "quantity_total", nullable = false)
    private Integer quantityTotal;

    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false, length = 20)
    private EquipmentCondition conditionStatus;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) isDeleted = false;
        if (conditionStatus == null) conditionStatus = EquipmentCondition.GOOD;
    }
@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Equipment() {
    }

    public Equipment(Long equipmentId, Laboratory laboratory, String equipmentCode, String name, String description, String category, String makeModel, String serialNumber, LocalDate purchaseDate, LocalDate warrantyExpiry, Integer quantityTotal, Integer quantityAvailable, EquipmentCondition conditionStatus, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.equipmentId = equipmentId;
        this.laboratory = laboratory;
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
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
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

    public Integer getQuantityTotal() {
        return quantityTotal;
    }

    public void setQuantityTotal(Integer quantityTotal) {
        this.quantityTotal = quantityTotal;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public EquipmentCondition getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(EquipmentCondition conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public Boolean isIsDeleted() {
        return isDeleted;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long equipmentId;
        private Laboratory laboratory;
        private String equipmentCode;
        private String name;
        private String description;
        private String category;
        private String makeModel;
        private String serialNumber;
        private LocalDate purchaseDate;
        private LocalDate warrantyExpiry;
        private Integer quantityTotal;
        private Integer quantityAvailable;
        private EquipmentCondition conditionStatus;
        private Boolean isDeleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder equipmentId(Long equipmentId) {
            this.equipmentId = equipmentId;
            return this;
        }
        public Builder laboratory(Laboratory laboratory) {
            this.laboratory = laboratory;
            return this;
        }
        public Builder equipmentCode(String equipmentCode) {
            this.equipmentCode = equipmentCode;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder category(String category) {
            this.category = category;
            return this;
        }
        public Builder makeModel(String makeModel) {
            this.makeModel = makeModel;
            return this;
        }
        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }
        public Builder purchaseDate(LocalDate purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }
        public Builder warrantyExpiry(LocalDate warrantyExpiry) {
            this.warrantyExpiry = warrantyExpiry;
            return this;
        }
        public Builder quantityTotal(Integer quantityTotal) {
            this.quantityTotal = quantityTotal;
            return this;
        }
        public Builder quantityAvailable(Integer quantityAvailable) {
            this.quantityAvailable = quantityAvailable;
            return this;
        }
        public Builder conditionStatus(EquipmentCondition conditionStatus) {
            this.conditionStatus = conditionStatus;
            return this;
        }
        public Builder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        public Equipment build() {
            return new Equipment(this.equipmentId, this.laboratory, this.equipmentCode, this.name, this.description, this.category, this.makeModel, this.serialNumber, this.purchaseDate, this.warrantyExpiry, this.quantityTotal, this.quantityAvailable, this.conditionStatus, this.isDeleted, this.createdAt, this.updatedAt);
        }
    }

}

