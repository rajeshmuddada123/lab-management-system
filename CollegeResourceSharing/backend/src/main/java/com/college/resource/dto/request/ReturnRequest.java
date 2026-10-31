package com.college.resource.dto.request;

public class ReturnRequest {
    private String conditionOnReturn;   // GOOD / FAIR / POOR / DAMAGED
    private String notes;


    public ReturnRequest() {
    }

    public ReturnRequest(String conditionOnReturn, String notes) {
        this.conditionOnReturn = conditionOnReturn;
        this.notes = notes;
    }

    public String getConditionOnReturn() {
        return conditionOnReturn;
    }

    public void setConditionOnReturn(String conditionOnReturn) {
        this.conditionOnReturn = conditionOnReturn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
