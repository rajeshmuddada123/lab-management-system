package com.college.resource.dto.request;

public class CollectionRequest {
    private String conditionOnCollection;   // GOOD / FAIR / POOR
    private String notes;


    public CollectionRequest() {
    }

    public CollectionRequest(String conditionOnCollection, String notes) {
        this.conditionOnCollection = conditionOnCollection;
        this.notes = notes;
    }

    public String getConditionOnCollection() {
        return conditionOnCollection;
    }

    public void setConditionOnCollection(String conditionOnCollection) {
        this.conditionOnCollection = conditionOnCollection;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
