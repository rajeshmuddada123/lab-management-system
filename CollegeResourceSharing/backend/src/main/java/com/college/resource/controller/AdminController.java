package com.college.resource.controller;

import com.college.resource.dto.request.*;
import com.college.resource.dto.response.ApiResponse;
import com.college.resource.entity.*;
import com.college.resource.enums.BookingStatus;
import com.college.resource.enums.DamageStatus;
import com.college.resource.repository.*;
import com.college.resource.security.UserPrincipal;
import com.college.resource.service.AdminService;
import com.college.resource.service.BookingService;
import com.college.resource.service.EquipmentService;
import com.opencsv.CSVReader;
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

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final BookingService bookingService;
    private final EquipmentService equipmentService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;
    private final WaitingListRepository waitingListRepository;
    private final DamageReportRepository damageReportRepository;
    private final UsageRuleRepository usageRuleRepository;

    public AdminController(AdminService adminService,
                           BookingService bookingService,
                           EquipmentService equipmentService,
                           UserRepository userRepository,
                           StudentRepository studentRepository,
                           FacultyRepository facultyRepository,
                           LaboratoryRepository laboratoryRepository,
                           EquipmentRepository equipmentRepository,
                           BookingRepository bookingRepository,
                           WaitingListRepository waitingListRepository,
                           DamageReportRepository damageReportRepository,
                           UsageRuleRepository usageRuleRepository) {
        this.adminService = adminService;
        this.bookingService = bookingService;
        this.equipmentService = equipmentService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.equipmentRepository = equipmentRepository;
        this.bookingRepository = bookingRepository;
        this.waitingListRepository = waitingListRepository;
        this.damageReportRepository = damageReportRepository;
        this.usageRuleRepository = usageRuleRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalFaculty", facultyRepository.count());
        stats.put("totalLabs", laboratoryRepository.count());
        stats.put("totalEquipment", equipmentRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("pendingBookings", bookingRepository.findByStatus(BookingStatus.PENDING, PageRequest.of(0, 1)).getTotalElements());
        stats.put("activeDamageReports", damageReportRepository.findByStatus(DamageStatus.REPORTED).size());
        stats.put("overdueBookings", bookingRepository.findOverdue(LocalDate.now()).size());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ── STUDENTS ──
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<Page<Student>>> listStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(adminService.listStudents(keyword, pageable)));
    }

    @PostMapping("/students")
    public ResponseEntity<ApiResponse<Student>> createStudent(
            @Valid @RequestBody CreateStudentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Student student = adminService.createStudent(request, user);
        return ResponseEntity.ok(ApiResponse.success("Student created successfully", student));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody CreateStudentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Student student = adminService.updateStudent(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", student));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateStudent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        adminService.deactivateStudent(id, user);
        return ResponseEntity.ok(ApiResponse.success("Student deactivated successfully", null));
    }

    @PostMapping(value = "/students/import-csv", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Map<String, Object>>> importStudentsCsv(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        int imported = 0;
        int failed = 0;
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            boolean header = true;
            while ((line = reader.readNext()) != null) {
                if (header) { header = false; continue; }
                if (line.length < 7) continue;
                try {
                    CreateStudentRequest req = new CreateStudentRequest();
                    req.setUsername(line[0].trim());
                    req.setEmail(line[1].trim());
                    req.setPassword(line[2].trim());
                    req.setRegistrationNumber(line[3].trim());
                    req.setFirstName(line[4].trim());
                    req.setLastName(line[5].trim());
                    req.setDepartment(line[6].trim());
                    req.setSemester(line.length > 7 ? Integer.parseInt(line[7].trim()) : 1);
                    req.setPhone(line.length > 8 ? line[8].trim() : null);
                    adminService.createStudent(req, user);
                    imported++;
                } catch (Exception ex) {
                    failed++;
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to parse CSV: " + e.getMessage()));
        }
        Map<String, Object> res = new HashMap<>();
        res.put("imported", imported);
        res.put("failed", failed);
        return ResponseEntity.ok(ApiResponse.success("CSV Import Completed", res));
    }

    // ── FACULTY ──
    @GetMapping("/faculty")
    public ResponseEntity<ApiResponse<Page<Faculty>>> listFaculty(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(adminService.listFaculty(keyword, pageable)));
    }

    @PostMapping("/faculty")
    public ResponseEntity<ApiResponse<Faculty>> createFaculty(
            @Valid @RequestBody CreateFacultyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Faculty faculty = adminService.createFaculty(request, user);
        return ResponseEntity.ok(ApiResponse.success("Faculty created successfully", faculty));
    }

    @DeleteMapping("/faculty/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateFaculty(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        adminService.deactivateFaculty(id, user);
        return ResponseEntity.ok(ApiResponse.success("Faculty deactivated successfully", null));
    }

    // ── LABS ──
    @GetMapping("/labs")
    public ResponseEntity<ApiResponse<List<Laboratory>>> listLabs() {
        return ResponseEntity.ok(ApiResponse.success(adminService.listLabs()));
    }

    @PostMapping("/labs")
    public ResponseEntity<ApiResponse<Laboratory>> createLab(
            @Valid @RequestBody LabRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Laboratory lab = adminService.createLab(request, user);
        return ResponseEntity.ok(ApiResponse.success("Laboratory created successfully", lab));
    }

    @PutMapping("/labs/{id}")
    public ResponseEntity<ApiResponse<Laboratory>> updateLab(
            @PathVariable Long id,
            @Valid @RequestBody LabRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Laboratory lab = adminService.updateLab(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("Laboratory updated successfully", lab));
    }

    @PostMapping("/faculty-assignments")
    public ResponseEntity<ApiResponse<FacultyLabAssignment>> assignFaculty(
            @Valid @RequestBody FacultyLabAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        FacultyLabAssignment assignment = adminService.assignFacultyToLab(request, user);
        return ResponseEntity.ok(ApiResponse.success("Faculty assigned to laboratory", assignment));
    }

    // ── EQUIPMENT ──
    @GetMapping("/equipment")
    public ResponseEntity<ApiResponse<Page<Equipment>>> listEquipment(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(equipmentService.browseEquipment(keyword, category, available, pageable)));
    }

    @PostMapping("/equipment")
    public ResponseEntity<ApiResponse<Equipment>> createEquipment(
            @Valid @RequestBody EquipmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Equipment eq = adminService.createEquipment(request, user);
        return ResponseEntity.ok(ApiResponse.success("Equipment created successfully", eq));
    }

    @PutMapping("/equipment/{id}")
    public ResponseEntity<ApiResponse<Equipment>> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        Equipment eq = adminService.updateEquipment(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("Equipment updated successfully", eq));
    }

    @DeleteMapping("/equipment/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEquipment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        adminService.deactivateEquipment(id, user);
        return ResponseEntity.ok(ApiResponse.success("Equipment deleted successfully", null));
    }

    @PostMapping(value = "/equipment/{id}/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<EquipmentImage>> uploadEquipmentImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        EquipmentImage img = equipmentService.uploadEquipmentImage(id, file, isPrimary, user);
        return ResponseEntity.ok(ApiResponse.success("Equipment image uploaded", img));
    }

    // ── BOOKINGS ──
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<Page<Booking>>> listBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(adminService.listAllBookings(status, pageable)));
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

    // ── WAITING LISTS ──
    @GetMapping("/waiting-lists")
    public ResponseEntity<ApiResponse<List<WaitingList>>> listWaitingLists() {
        return ResponseEntity.ok(ApiResponse.success(waitingListRepository.findAll()));
    }

    // ── DAMAGE REPORTS ──
    @GetMapping("/damage-reports")
    public ResponseEntity<ApiResponse<Page<DamageReport>>> listDamageReports(
            @RequestParam(required = false) DamageStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(adminService.listDamageReports(status, pageable)));
    }

    @PutMapping("/damage-reports/{id}/status")
    public ResponseEntity<ApiResponse<DamageReport>> updateDamageStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        DamageReport dr = adminService.updateDamageStatus(id, status, user);
        return ResponseEntity.ok(ApiResponse.success("Damage report status updated", dr));
    }

    // ── USAGE RULES ──
    @GetMapping("/usage-rules/{labId}")
    public ResponseEntity<ApiResponse<UsageRule>> getUsageRule(@PathVariable Long labId) {
        return ResponseEntity.ok(ApiResponse.success(usageRuleRepository.findByLaboratoryLabId(labId).orElse(null)));
    }

    @PostMapping("/usage-rules")
    public ResponseEntity<ApiResponse<UsageRule>> setUsageRule(
            @Valid @RequestBody UsageRuleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId()).orElseThrow();
        UsageRule rule = adminService.setUsageRule(request, user);
        return ResponseEntity.ok(ApiResponse.success("Usage rule updated successfully", rule));
    }

    // ── AUDIT LOGS ──
    @GetMapping("/activity-logs")
    public ResponseEntity<ApiResponse<Page<ActivityLog>>> listActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(adminService.listActivityLogs(pageable)));
    }
}
