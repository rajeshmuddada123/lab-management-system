package com.college.resource.controller;

import com.college.resource.dto.request.ApprovalRequest;
import com.college.resource.dto.request.DamageReportRequest;
import com.college.resource.dto.response.ApiResponse;
import com.college.resource.entity.*;
import com.college.resource.enums.BookingStatus;
import com.college.resource.exception.ResourceNotFoundException;
import com.college.resource.repository.*;
import com.college.resource.security.UserPrincipal;
import com.college.resource.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faculty")
@PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
public class FacultyController {

    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final EquipmentService equipmentService;
    private final BookingService bookingService;
    private final CollectionReturnService collectionReturnService;
    private final DamageReportService damageReportService;
    private final NotificationService notificationService;
    private final BookingRepository bookingRepository;

    public FacultyController(FacultyRepository facultyRepository,
                             UserRepository userRepository,
                             EquipmentService equipmentService,
                             BookingService bookingService,
                             CollectionReturnService collectionReturnService,
                             DamageReportService damageReportService,
                             NotificationService notificationService,
                             BookingRepository bookingRepository) {
        this.facultyRepository = facultyRepository;
        this.userRepository = userRepository;
        this.equipmentService = equipmentService;
        this.bookingService = bookingService;
        this.collectionReturnService = collectionReturnService;
        this.damageReportService = damageReportService;
        this.notificationService = notificationService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Faculty>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        Faculty faculty = facultyRepository.findByUserUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        return ResponseEntity.ok(ApiResponse.success(faculty));
    }

    @GetMapping("/labs")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getAssignedLabs(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getFacultyLabs(user)));
    }

    @GetMapping("/equipment")
    public ResponseEntity<ApiResponse<Page<Equipment>>> getEquipmentForLab(
            @RequestParam Long labId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(equipmentService.getEquipmentByLab(labId, category, available, pageable)));
    }

    @PutMapping("/equipment/{id}/availability")
    public ResponseEntity<ApiResponse<Void>> updateEquipmentAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        int availableQty = Integer.parseInt(body.get("availableQty").toString());
        String condition = body.containsKey("condition") ? (String) body.get("condition") : null;
        equipmentService.updateEquipmentAvailability(id, availableQty, condition, user);
        return ResponseEntity.ok(ApiResponse.success("Equipment availability updated", null));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<Page<Booking>>> getBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingsForFaculty(user, pageable)));
    }

    @GetMapping("/bookings/pending")
    public ResponseEntity<ApiResponse<Page<Booking>>> getPendingBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(bookingService.getPendingBookingsForFaculty(user, pageable)));
    }

    @PostMapping("/bookings/{id}/approval")
    public ResponseEntity<ApiResponse<Booking>> approveOrRejectBooking(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Booking booking = bookingService.approveBooking(id, request.getAction(), request.getNotes(), user);
        return ResponseEntity.ok(ApiResponse.success("Booking " + request.getAction() + " successfully", booking));
    }

    @PostMapping(value = "/bookings/{id}/collection", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<CollectionRecord>> verifyCollection(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "GOOD") String condition,
            @RequestParam(required = false) String notes,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        CollectionRecord record = collectionReturnService.recordCollection(id, condition, notes, photo, user);
        return ResponseEntity.ok(ApiResponse.success("Collection verified successfully", record));
    }

    @PostMapping(value = "/bookings/{id}/return", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ReturnRecord>> verifyReturn(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "GOOD") String condition,
            @RequestParam(required = false) String notes,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        ReturnRecord record = collectionReturnService.recordReturn(id, condition, notes, photo, user);
        return ResponseEntity.ok(ApiResponse.success("Return verified successfully", record));
    }

    @PostMapping(value = "/damage-reports", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<DamageReport>> reportDamage(
            @ModelAttribute @Valid DamageReportRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        DamageReport dr = damageReportService.reportDamage(request, photo, user);
        return ResponseEntity.ok(ApiResponse.success("Damage report submitted", dr));
    }

    @PostMapping(value = "/condition-reports", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ConditionReport>> reportCondition(
            @RequestParam Long equipmentId,
            @RequestParam String conditionStatus,
            @RequestParam String description,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        ConditionReport cr = damageReportService.submitConditionReport(equipmentId, conditionStatus, description, photo, user);
        return ResponseEntity.ok(ApiResponse.success("Condition report submitted", cr));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<Booking>>> getOverdueEquipment() {
        return ResponseEntity.ok(ApiResponse.success(bookingRepository.findOverdue(LocalDate.now())));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<Page<Notification>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUserNotifications(principal.getUserId(), pageable)));
    }
}
