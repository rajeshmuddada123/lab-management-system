package com.college.resource.controller;

import com.college.resource.dto.request.LoginRequest;
import com.college.resource.dto.response.ApiResponse;
import com.college.resource.dto.response.JwtResponse;
import com.college.resource.entity.Faculty;
import com.college.resource.entity.Student;
import com.college.resource.entity.User;
import com.college.resource.repository.FacultyRepository;
import com.college.resource.repository.StudentRepository;
import com.college.resource.repository.UserRepository;
import com.college.resource.security.UserPrincipal;
import com.college.resource.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          StudentRepository studentRepository,
                          FacultyRepository facultyRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request,
                                                          HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        JwtResponse response = authService.login(request, ip);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthenticated"));
        }
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("role", user.getRole().getRoleName());
        data.put("status", user.getStatus());

        if ("STUDENT".equals(user.getRole().getRoleName())) {
            studentRepository.findByUserUserId(user.getUserId()).ifPresent(s -> {
                data.put("studentId", s.getStudentId());
                data.put("registrationNumber", s.getRegistrationNumber());
                data.put("firstName", s.getFirstName());
                data.put("lastName", s.getLastName());
                data.put("fullName", s.getFullName());
                data.put("department", s.getDepartment());
                data.put("semester", s.getSemester());
                data.put("phone", s.getPhone());
            });
        } else if ("FACULTY".equals(user.getRole().getRoleName())) {
            facultyRepository.findByUserUserId(user.getUserId()).ifPresent(f -> {
                data.put("facultyId", f.getFacultyId());
                data.put("facultyCode", f.getFacultyCode());
                data.put("firstName", f.getFirstName());
                data.put("lastName", f.getLastName());
                data.put("fullName", f.getFullName());
                data.put("department", f.getDepartment());
                data.put("designation", f.getDesignation());
                data.put("phone", f.getPhone());
            });
        }
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
