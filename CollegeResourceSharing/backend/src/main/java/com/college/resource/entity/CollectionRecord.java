package com.college.resource.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "collection_records")
public class CollectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private Long collectionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by", nullable = false)
    private User collectedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    @Column(name = "condition_on_collection", nullable = false, length = 20)
    private String conditionOnCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_file_id")
    private FileAttachment photo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

@PrePersist
    protected void onCreate() {
        if (collectedAt == null) collectedAt = LocalDateTime.now();
    }

    public CollectionRecord() {
    }

    public CollectionRecord(Long collectionId, Booking booking, User collectedBy, User verifiedBy, LocalDateTime collectedAt, String conditionOnCollection, FileAttachment photo, String notes) {
        this.collectionId = collectionId;
        this.booking = booking;
        this.collectedBy = collectedBy;
        this.verifiedBy = verifiedBy;
        this.collectedAt = collectedAt;
        this.conditionOnCollection = conditionOnCollection;
        this.photo = photo;
        this.notes = notes;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public User getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(User collectedBy) {
        this.collectedBy = collectedBy;
    }

    public User getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(User verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(LocalDateTime collectedAt) {
        this.collectedAt = collectedAt;
    }

    public String getConditionOnCollection() {
        return conditionOnCollection;
    }

    public void setConditionOnCollection(String conditionOnCollection) {
        this.conditionOnCollection = conditionOnCollection;
    }

    public FileAttachment getPhoto() {
        return photo;
    }

    public void setPhoto(FileAttachment photo) {
        this.photo = photo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long collectionId;
        private Booking booking;
        private User collectedBy;
        private User verifiedBy;
        private LocalDateTime collectedAt;
        private String conditionOnCollection;
        private FileAttachment photo;
        private String notes;
        public Builder collectionId(Long collectionId) {
            this.collectionId = collectionId;
            return this;
        }
        public Builder booking(Booking booking) {
            this.booking = booking;
            return this;
        }
        public Builder collectedBy(User collectedBy) {
            this.collectedBy = collectedBy;
            return this;
        }
        public Builder verifiedBy(User verifiedBy) {
            this.verifiedBy = verifiedBy;
            return this;
        }
        public Builder collectedAt(LocalDateTime collectedAt) {
            this.collectedAt = collectedAt;
            return this;
        }
        public Builder conditionOnCollection(String conditionOnCollection) {
            this.conditionOnCollection = conditionOnCollection;
            return this;
        }
        public Builder photo(FileAttachment photo) {
            this.photo = photo;
            return this;
        }
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }
        public CollectionRecord build() {
            return new CollectionRecord(this.collectionId, this.booking, this.collectedBy, this.verifiedBy, this.collectedAt, this.conditionOnCollection, this.photo, this.notes);
        }
    }

}

