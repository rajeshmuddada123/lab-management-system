package com.college.resource.controller;

import com.college.resource.dto.request.BookingRequest;
import com.college.resource.dto.response.ApiResponse;
import com.college.resource.entity.*;
import com.college.resource.enums.BookingStatus;
import com.college.resource.exception.ResourceNotFoundException;
import com.college.resource.repository.*;
import com.college.resource.security.UserPrincipal;
import com.college.resource.service.BookingService;
import com.college.resource.service.EquipmentService;
import com.college.resource.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EquipmentService equipmentService;
    private final BookingService bookingService;
    private final NotificationService notificationService;
    private final WaitingListRepository waitingListRepository;

    public StudentController(StudentRepository studentRepository,
                             UserRepository userRepository,
                             EquipmentService equipmentService,
                             BookingService bookingService,
                             NotificationService notificationService,
                             WaitingListRepository waitingListRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.equipmentService = equipmentService;
        this.bookingService = bookingService;
        this.notificationService = notificationService;
        this.waitingListRepository = waitingListRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Student>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        Student student = studentRepository.findByUserUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    @GetMapping("/labs")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getLabs() {
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getActiveLabs()));
    }

    @GetMapping("/equipment")
    public ResponseEntity<ApiResponse<Page<Equipment>>> browseEquipment(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(equipmentService.browseEquipment(keyword, category, available, pageable)));
    }

    @GetMapping("/equipment/{id}")
    public ResponseEntity<ApiResponse<Equipment>> getEquipmentDetails(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getEquipmentById(id)));
    }

    @GetMapping("/equipment/{id}/images")
    public ResponseEntity<ApiResponse<List<EquipmentImage>>> getEquipmentImages(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getEquipmentImages(id)));
    }

    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<Booking>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Booking booking = bookingService.createBooking(request, user);
        return ResponseEntity.ok(ApiResponse.success("Booking request submitted successfully", booking));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<Page<Booking>>> getBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Student student = studentRepository.findByUserUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(bookingService.getStudentBookings(student.getStudentId(), status, pageable)));
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Booking booking = bookingService.cancelBooking(id, user);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }

    @GetMapping("/waitlist")
    public ResponseEntity<ApiResponse<Page<WaitingList>>> getWaitlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Student student = studentRepository.findByUserUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(waitingListRepository.findByStudentStudentId(student.getStudentId(), pageable)));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<Page<Notification>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUserNotifications(principal.getUserId(), pageable)));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markNotificationRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markOneRead(id, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsRead(
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllRead(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
