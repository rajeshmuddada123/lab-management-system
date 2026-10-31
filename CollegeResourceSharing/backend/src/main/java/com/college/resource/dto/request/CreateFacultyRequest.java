package com.college.resource.dto.request;

import jakarta.validation.constraints.*;
public class CreateFacultyRequest {
    @NotBlank private String username;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank private String facultyCode;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String department;
    @NotBlank private String designation;
    private String phone;


    public CreateFacultyRequest() {
    }

    public CreateFacultyRequest(String username, String email, String password, String facultyCode, String firstName, String lastName, String department, String designation, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.facultyCode = facultyCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.designation = designation;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

}
