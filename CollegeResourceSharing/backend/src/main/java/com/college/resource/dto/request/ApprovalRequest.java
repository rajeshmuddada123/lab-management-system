package com.college.resource.dto.request;

import com.college.resource.enums.ApprovalAction;
import jakarta.validation.constraints.NotNull;
public class ApprovalRequest {
    @NotNull private ApprovalAction action;
    private String notes;


    public ApprovalRequest() {
    }

    public ApprovalRequest(ApprovalAction action, String notes) {
        this.action = action;
        this.notes = notes;
    }

    public ApprovalAction getAction() {
        return action;
    }

    public void setAction(ApprovalAction action) {
        this.action = action;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
