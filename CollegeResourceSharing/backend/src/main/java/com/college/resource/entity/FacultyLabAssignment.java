package com.college.resource.entity;

import com.college.resource.enums.AssignmentRole;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "faculty_lab_assignments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"faculty_id","lab_id"}))
public class FacultyLabAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", nullable = false)
    private Laboratory laboratory;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_role", nullable = false, length = 20)
    private AssignmentRole assignmentRole;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (assignmentRole == null) assignmentRole = AssignmentRole.ASSISTANT;
    }

    public FacultyLabAssignment() {
    }

    public FacultyLabAssignment(Long assignmentId, Faculty faculty, Laboratory laboratory, AssignmentRole assignmentRole, LocalDate assignedDate, LocalDate endDate, LocalDateTime createdAt) {
        this.assignmentId = assignmentId;
        this.faculty = faculty;
        this.laboratory = laboratory;
        this.assignmentRole = assignmentRole;
        this.assignedDate = assignedDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
    }

    public AssignmentRole getAssignmentRole() {
        return assignmentRole;
    }

    public void setAssignmentRole(AssignmentRole assignmentRole) {
        this.assignmentRole = assignmentRole;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long assignmentId;
        private Faculty faculty;
        private Laboratory laboratory;
        private AssignmentRole assignmentRole;
        private LocalDate assignedDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;
        public Builder assignmentId(Long assignmentId) {
            this.assignmentId = assignmentId;
            return this;
        }
        public Builder faculty(Faculty faculty) {
            this.faculty = faculty;
            return this;
        }
        public Builder laboratory(Laboratory laboratory) {
            this.laboratory = laboratory;
            return this;
        }
        public Builder assignmentRole(AssignmentRole assignmentRole) {
            this.assignmentRole = assignmentRole;
            return this;
        }
        public Builder assignedDate(LocalDate assignedDate) {
            this.assignedDate = assignedDate;
            return this;
        }
        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public FacultyLabAssignment build() {
            return new FacultyLabAssignment(this.assignmentId, this.faculty, this.laboratory, this.assignmentRole, this.assignedDate, this.endDate, this.createdAt);
        }
    }

}

