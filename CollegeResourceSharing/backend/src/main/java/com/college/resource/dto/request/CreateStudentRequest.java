package com.college.resource.dto.request;

import jakarta.validation.constraints.*;
public class CreateStudentRequest {
    @NotBlank private String username;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") private String password;
    @NotBlank private String registrationNumber;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String department;
    @Min(1) @Max(12) private int semester;
    private String phone;
    private String address;


    public CreateStudentRequest() {
    }

    public CreateStudentRequest(String username, String email, String password, String registrationNumber, String firstName, String lastName, String department, int semester, String phone, String address) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.semester = semester;
        this.phone = phone;
        this.address = address;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
