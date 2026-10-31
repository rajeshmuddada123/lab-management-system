package com.college.resource.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "faculty")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faculty_id")
    private Long facultyId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "faculty_code", nullable = false, unique = true, length = 20)
    private String facultyCode;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Faculty() {
    }

    public Faculty(Long facultyId, User user, String facultyCode, String firstName, String lastName, String department, String designation, String phone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.facultyId = facultyId;
        this.user = user;
        this.facultyCode = facultyCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.designation = designation;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFacultyCode() {
        return facultyCode;
    }

    public void setFacultyCode(String facultyCode) {
        this.facultyCode = facultyCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        private Long facultyId;
        private User user;
        private String facultyCode;
        private String firstName;
        private String lastName;
        private String department;
        private String designation;
        private String phone;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder facultyId(Long facultyId) {
            this.facultyId = facultyId;
            return this;
        }
        public Builder user(User user) {
            this.user = user;
            return this;
        }
        public Builder facultyCode(String facultyCode) {
            this.facultyCode = facultyCode;
            return this;
        }
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        public Builder department(String department) {
            this.department = department;
            return this;
        }
        public Builder designation(String designation) {
            this.designation = designation;
            return this;
        }
        public Builder phone(String phone) {
            this.phone = phone;
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
        public Faculty build() {
            return new Faculty(this.facultyId, this.user, this.facultyCode, this.firstName, this.lastName, this.department, this.designation, this.phone, this.createdAt, this.updatedAt);
        }
    }

}


