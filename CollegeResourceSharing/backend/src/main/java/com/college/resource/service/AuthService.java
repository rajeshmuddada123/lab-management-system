package com.college.resource.service;

import com.college.resource.dto.request.LoginRequest;
import com.college.resource.dto.response.JwtResponse;
import com.college.resource.entity.*;
import com.college.resource.enums.UserStatus;
import com.college.resource.exception.BadRequestException;
import com.college.resource.repository.*;
import com.college.resource.security.*;
import com.college.resource.util.AuditLogger;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AuditLogger auditLogger;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils,
                       UserRepository userRepository,
                       StudentRepository studentRepository,
                       FacultyRepository facultyRepository,
                       AuditLogger auditLogger) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils              = jwtUtils;
        this.userRepository        = userRepository;
        this.studentRepository     = studentRepository;
        this.facultyRepository     = facultyRepository;
        this.auditLogger           = auditLogger;
    }

    /**
     * Unified login:
     *  - Students   login with their registration number (mapped to username in users table)
     *  - Faculty    login with faculty code (mapped to username)
     *  - Admin      login with username or email
     *
     * The username field in the login request is resolved to the users.username
     * by attempting: (1) direct username match, (2) student reg_number, (3) faculty_code.
     */
    @Transactional
    public JwtResponse login(LoginRequest request, String ip) {
        String resolvedUsername = resolveUsername(request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(resolvedUsername, request.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtils.generateToken(authentication);

        // Update last_login_at
        userRepository.findById(principal.getUserId()).ifPresent(u -> {
            u.setLastLoginAt(LocalDateTime.now());
            userRepository.save(u);
            auditLogger.log(u, "USER_LOGIN", "users", u.getUserId(),
                    "Login: " + u.getUsername(), ip);
        });

        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .status(user.getStatus().name())
                .build();
    }

    private String resolveUsername(String identifier) {
        // 1. Try direct username
        if (userRepository.findByUsernameAndIsDeletedFalse(identifier).isPresent()) {
            return identifier;
        }
        // 2. Try student registration_number → get associated username
        var studentOpt = studentRepository.findByRegistrationNumber(identifier);
        if (studentOpt.isPresent()) {
            User u = studentOpt.get().getUser();
            if (u.getIsDeleted()) throw new BadRequestException("Account is deactivated");
            return u.getUsername();
        }
        // 3. Try faculty code
        var facultyOpt = facultyRepository.findByFacultyCode(identifier);
        if (facultyOpt.isPresent()) {
            User u = facultyOpt.get().getUser();
            if (u.getIsDeleted()) throw new BadRequestException("Account is deactivated");
            return u.getUsername();
        }
        // 4. Try email
        if (userRepository.findByEmailAndIsDeletedFalse(identifier).isPresent()) {
            return userRepository.findByEmailAndIsDeletedFalse(identifier).get().getUsername();
        }
        throw new UsernameNotFoundException("No account found for: " + identifier);
    }
}
