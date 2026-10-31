package com.college.resource.dto.request;

import com.college.resource.enums.AssignmentRole;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FacultyLabAssignmentRequest {
    @NotNull private Long facultyId;
    @NotNull private Long labId;
    private AssignmentRole assignmentRole;
    @NotNull private LocalDate assignedDate;
    private LocalDate endDate;


    public FacultyLabAssignmentRequest() {
    }

    public FacultyLabAssignmentRequest(Long facultyId, Long labId, AssignmentRole assignmentRole, LocalDate assignedDate, LocalDate endDate) {
        this.facultyId = facultyId;
        this.labId = labId;
        this.assignmentRole = assignmentRole;
        this.assignedDate = assignedDate;
        this.endDate = endDate;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
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

}
