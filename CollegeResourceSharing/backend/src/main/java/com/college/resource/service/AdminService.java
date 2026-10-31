package com.college.resource.service;

import com.college.resource.dto.request.*;
import com.college.resource.entity.*;
import com.college.resource.enums.*;
import com.college.resource.exception.*;
import com.college.resource.repository.*;
import com.college.resource.util.AuditLogger;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository         userRepository;
    private final StudentRepository      studentRepository;
    private final FacultyRepository      facultyRepository;
    private final LaboratoryRepository   labRepository;
    private final EquipmentRepository    equipmentRepository;
    private final FacultyLabAssignmentRepository flaRepository;
    private final BookingRepository      bookingRepository;
    private final UsageRuleRepository    usageRuleRepository;
    private final DamageReportRepository damageReportRepository;
    private final ActivityLogRepository  activityLogRepository;
    private final PasswordEncoder        passwordEncoder;
    private final AuditLogger            auditLogger;

    public AdminService(UserRepository ur, StudentRepository sr, FacultyRepository fr,
                        LaboratoryRepository lr, EquipmentRepository er,
                        FacultyLabAssignmentRepository flar,
                        BookingRepository br, UsageRuleRepository urr,
                        DamageReportRepository drr, ActivityLogRepository alr,
                        PasswordEncoder pe, AuditLogger al) {
        this.userRepository = ur; this.studentRepository = sr; this.facultyRepository = fr;
        this.labRepository = lr; this.equipmentRepository = er; this.flaRepository = flar;
        this.bookingRepository = br; this.usageRuleRepository = urr;
        this.damageReportRepository = drr; this.activityLogRepository = alr;
        this.passwordEncoder = pe; this.auditLogger = al;
    }

    // ── STUDENT MANAGEMENT ───────────────────────────────────

    @Transactional
    public Student createStudent(CreateStudentRequest req, User adminUser) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new BadRequestException("Username already taken: " + req.getUsername());
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already registered: " + req.getEmail());
        if (studentRepository.existsByRegistrationNumber(req.getRegistrationNumber()))
            throw new BadRequestException("Registration number already exists: " + req.getRegistrationNumber());

        Role studentRole = getRoleByName("STUDENT");
        User user = User.builder()
                .username(req.getUsername()).email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(studentRole).status(UserStatus.ACTIVE).build();
        user = userRepository.save(user);

        Student student = Student.builder()
                .user(user).registrationNumber(req.getRegistrationNumber())
                .firstName(req.getFirstName()).lastName(req.getLastName())
                .department(req.getDepartment()).semester(req.getSemester())
                .phone(req.getPhone()).address(req.getAddress()).build();
        student = studentRepository.save(student);
        auditLogger.log(adminUser, "USER_CREATED", "students", student.getStudentId(),
                "Created student: " + req.getRegistrationNumber());
        return student;
    }

    @Transactional
    public Student updateStudent(Long studentId, CreateStudentRequest req, User adminUser) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        s.setFirstName(req.getFirstName()); s.setLastName(req.getLastName());
        s.setDepartment(req.getDepartment()); s.setSemester(req.getSemester());
        s.setPhone(req.getPhone()); s.setAddress(req.getAddress());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            s.getUser().setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        auditLogger.log(adminUser, "USER_UPDATED", "students", studentId, "Updated student");
        return studentRepository.save(s);
    }

    @Transactional
    public void deactivateStudent(Long studentId, User adminUser) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        s.getUser().setStatus(UserStatus.INACTIVE);
        userRepository.save(s.getUser());
        auditLogger.log(adminUser, "USER_DEACTIVATED", "students", studentId, "Deactivated student");
    }

    public Page<Student> listStudents(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) return studentRepository.searchStudents(keyword, pageable);
        return studentRepository.findAll(pageable);
    }

    // ── FACULTY MANAGEMENT ───────────────────────────────────

    @Transactional
    public Faculty createFaculty(CreateFacultyRequest req, User adminUser) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new BadRequestException("Username already taken: " + req.getUsername());
        if (facultyRepository.existsByFacultyCode(req.getFacultyCode()))
            throw new BadRequestException("Faculty code already exists: " + req.getFacultyCode());

        Role facultyRole = getRoleByName("FACULTY");
        User user = User.builder()
                .username(req.getUsername()).email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(facultyRole).status(UserStatus.ACTIVE).build();
        user = userRepository.save(user);

        Faculty faculty = Faculty.builder()
                .user(user).facultyCode(req.getFacultyCode())
                .firstName(req.getFirstName()).lastName(req.getLastName())
                .department(req.getDepartment()).designation(req.getDesignation())
                .phone(req.getPhone()).build();
        faculty = facultyRepository.save(faculty);
        auditLogger.log(adminUser, "USER_CREATED", "faculty", faculty.getFacultyId(),
                "Created faculty: " + req.getFacultyCode());
        return faculty;
    }

    @Transactional
    public void deactivateFaculty(Long facultyId, User adminUser) {
        Faculty f = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", facultyId));
        f.getUser().setStatus(UserStatus.INACTIVE);
        userRepository.save(f.getUser());
        auditLogger.log(adminUser, "USER_DEACTIVATED", "faculty", facultyId, "Deactivated faculty");
    }

    // ── LAB MANAGEMENT ───────────────────────────────────────

    @Transactional
    public Laboratory createLab(LabRequest req, User adminUser) {
        if (labRepository.existsByLabCode(req.getLabCode()))
            throw new BadRequestException("Lab code already exists: " + req.getLabCode());
        Laboratory lab = Laboratory.builder()
                .labCode(req.getLabCode()).labName(req.getLabName())
                .location(req.getLocation()).capacity(req.getCapacity())
                .status(req.getStatus() != null ? req.getStatus() : LabStatus.ACTIVE)
                .description(req.getDescription()).build();
        lab = labRepository.save(lab);
        auditLogger.log(adminUser, "LAB_CREATED", "laboratories", lab.getLabId(),
                "Created lab: " + req.getLabCode());
        return lab;
    }

    @Transactional
    public Laboratory updateLab(Long labId, LabRequest req, User adminUser) {
        Laboratory lab = labRepository.findById(labId)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory", labId));
        lab.setLabName(req.getLabName()); lab.setLocation(req.getLocation());
        lab.setCapacity(req.getCapacity());
        if (req.getStatus() != null) lab.setStatus(req.getStatus());
        lab.setDescription(req.getDescription());
        auditLogger.log(adminUser, "LAB_UPDATED", "laboratories", labId, "Updated lab");
        return labRepository.save(lab);
    }

    // ── FACULTY–LAB ASSIGNMENT ────────────────────────────────

    @Transactional
    public FacultyLabAssignment assignFacultyToLab(FacultyLabAssignmentRequest req, User adminUser) {
        Faculty f = facultyRepository.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", req.getFacultyId()));
        Laboratory lab = labRepository.findById(req.getLabId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory", req.getLabId()));
        if (flaRepository.existsByFacultyFacultyIdAndLaboratoryLabId(req.getFacultyId(), req.getLabId()))
            throw new BadRequestException("Faculty already assigned to this lab");

        FacultyLabAssignment fla = FacultyLabAssignment.builder()
                .faculty(f).laboratory(lab)
                .assignmentRole(req.getAssignmentRole() != null ? req.getAssignmentRole() : AssignmentRole.ASSISTANT)
                .assignedDate(req.getAssignedDate()).endDate(req.getEndDate()).build();
        fla = flaRepository.save(fla);
        auditLogger.log(adminUser, "FACULTY_LAB_ASSIGNED", "faculty_lab_assignments",
                fla.getAssignmentId(), "Faculty " + f.getFacultyCode() + " assigned to " + lab.getLabCode());
        return fla;
    }

    // ── EQUIPMENT MANAGEMENT ─────────────────────────────────

    @Transactional
    public Equipment createEquipment(EquipmentRequest req, User adminUser) {
        if (equipmentRepository.existsByEquipmentCode(req.getEquipmentCode()))
            throw new BadRequestException("Equipment code already exists: " + req.getEquipmentCode());
        Laboratory lab = labRepository.findById(req.getLabId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory", req.getLabId()));
        Equipment eq = Equipment.builder()
                .laboratory(lab).equipmentCode(req.getEquipmentCode())
                .name(req.getName()).description(req.getDescription())
                .category(req.getCategory()).makeModel(req.getMakeModel())
                .serialNumber(req.getSerialNumber())
                .purchaseDate(req.getPurchaseDate()).warrantyExpiry(req.getWarrantyExpiry())
                .quantityTotal(req.getQuantityTotal()).quantityAvailable(req.getQuantityAvailable())
                .conditionStatus(req.getConditionStatus() != null
                        ? EquipmentCondition.valueOf(req.getConditionStatus()) : EquipmentCondition.GOOD)
                .build();
        eq = equipmentRepository.save(eq);
        auditLogger.log(adminUser, "EQUIPMENT_CREATED", "equipment", eq.getEquipmentId(),
                "Created equipment: " + req.getEquipmentCode());
        return eq;
    }

    @Transactional
    public Equipment updateEquipment(Long id, EquipmentRequest req, User adminUser) {
        Equipment eq = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id));
        if (req.getLabId() != null) {
            Laboratory lab = labRepository.findById(req.getLabId())
                    .orElseThrow(() -> new ResourceNotFoundException("Laboratory", req.getLabId()));
            eq.setLaboratory(lab);
        }
        eq.setName(req.getName()); eq.setDescription(req.getDescription());
        eq.setCategory(req.getCategory()); eq.setMakeModel(req.getMakeModel());
        eq.setQuantityTotal(req.getQuantityTotal());
        eq.setQuantityAvailable(req.getQuantityAvailable());
        auditLogger.log(adminUser, "EQUIPMENT_UPDATED", "equipment", id, "Updated equipment");
        return equipmentRepository.save(eq);
    }

    @Transactional
    public void deactivateEquipment(Long id, User adminUser) {
        Equipment eq = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id));
        eq.setIsDeleted(true);
        equipmentRepository.save(eq);
        auditLogger.log(adminUser, "EQUIPMENT_DELETED", "equipment", id, "Soft-deleted equipment");
    }

    // ── BOOKING MANAGEMENT ───────────────────────────────────

    public Page<Booking> listAllBookings(BookingStatus status, Pageable pageable) {
        if (status != null) return bookingRepository.findByStatus(status, pageable);
        return bookingRepository.findAll(pageable);
    }

    // ── USAGE RULES ──────────────────────────────────────────

    @Transactional
    public UsageRule setUsageRule(UsageRuleRequest req, User adminUser) {
        Laboratory lab = labRepository.findById(req.getLabId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory", req.getLabId()));
        UsageRule rule = usageRuleRepository.findByLaboratoryLabId(req.getLabId())
                .orElse(UsageRule.builder().laboratory(lab).build());
        rule.setMaxBookingDurationHours(req.getMaxBookingDurationHours());
        rule.setMaxAdvanceBookingDays(req.getMaxAdvanceBookingDays());
        rule.setMaxConcurrentBookingsPerStudent(req.getMaxConcurrentBookingsPerStudent());
        rule.setAllowedDays(req.getAllowedDays());
        rule.setOpenTime(req.getOpenTime());
        rule.setCloseTime(req.getCloseTime());
        rule.setNotes(req.getNotes());
        return usageRuleRepository.save(rule);
    }

    // ── DAMAGE REPORTS ───────────────────────────────────────

    public Page<DamageReport> listDamageReports(DamageStatus status, Pageable pageable) {
        if (status != null) return damageReportRepository.findByStatus(status, pageable);
        return damageReportRepository.findAll(pageable);
    }

    @Transactional
    public DamageReport updateDamageStatus(Long damageId, String status, User adminUser) {
        DamageReport dr = damageReportRepository.findById(damageId)
                .orElseThrow(() -> new ResourceNotFoundException("DamageReport", damageId));
        dr.setStatus(DamageStatus.valueOf(status));
        if (dr.getStatus() == DamageStatus.RESOLVED) dr.setResolvedAt(java.time.LocalDateTime.now());
        return damageReportRepository.save(dr);
    }

    // ── ACTIVITY LOGS ─────────────────────────────────────────

    public Page<ActivityLog> listActivityLogs(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // ── HELPERS ───────────────────────────────────────────────

    private Role getRoleByName(String name) {
        return userRepository.findAll().stream()
                .map(User::getRole).filter(r -> r.getRoleName().equals(name))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name));
    }

    public List<Laboratory> listLabs() {
        return labRepository.findAll();
    }

    public Page<Faculty> listFaculty(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) return facultyRepository.searchFaculty(keyword, pageable);
        return facultyRepository.findAll(pageable);
    }
}
