package com.college.resource.config;

import com.college.resource.entity.*;
import com.college.resource.enums.*;
import com.college.resource.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final FacultyLabAssignmentRepository flaRepository;
    private final EquipmentRepository equipmentRepository;
    private final UsageRuleRepository usageRuleRepository;
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           StudentRepository studentRepository,
                           FacultyRepository facultyRepository,
                           LaboratoryRepository laboratoryRepository,
                           FacultyLabAssignmentRepository flaRepository,
                           EquipmentRepository equipmentRepository,
                           UsageRuleRepository usageRuleRepository,
                           BookingRepository bookingRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.flaRepository = flaRepository;
        this.equipmentRepository = equipmentRepository;
        this.usageRuleRepository = usageRuleRepository;
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Database already initialized with seed data.");
            return;
        }

        log.info("Starting automatic database seed initialization...");

        // 1. Roles
        Role adminRole = roleRepository.save(new Role(null, "ADMIN", LocalDateTime.now()));
        Role studentRole = roleRepository.save(new Role(null, "STUDENT", LocalDateTime.now()));
        Role facultyRole = roleRepository.save(new Role(null, "FACULTY", LocalDateTime.now()));

        String hash = passwordEncoder.encode("Password@123");

        // 2. Admin User
        User adminUser = User.builder()
                .username("admin")
                .email("admin@college.edu")
                .passwordHash(hash)
                .role(adminRole)
                .status(UserStatus.ACTIVE)
                .isDeleted(false)
                .build();
        userRepository.save(adminUser);

        // 3. Student User & Profile (Arun Kumar)
        User studentUser = User.builder()
                .username("student.arun")
                .email("arun.k@college.edu")
                .passwordHash(hash)
                .role(studentRole)
                .status(UserStatus.ACTIVE)
                .isDeleted(false)
                .build();
        studentUser = userRepository.save(studentUser);

        Student student = Student.builder()
                .user(studentUser)
                .registrationNumber("CS2024001")
                .firstName("Arun")
                .lastName("Kumar")
                .department("Computer Science")
                .semester(5)
                .phone("9876543210")
                .build();
        student = studentRepository.save(student);

        // Student 2 (Priya Sharma)
        User studentUser2 = User.builder()
                .username("student.priya")
                .email("priya.s@college.edu")
                .passwordHash(hash)
                .role(studentRole)
                .status(UserStatus.ACTIVE)
                .isDeleted(false)
                .build();
        studentUser2 = userRepository.save(studentUser2);

        studentRepository.save(Student.builder()
                .user(studentUser2)
                .registrationNumber("CS2024002")
                .firstName("Priya")
                .lastName("Sharma")
                .department("Computer Science")
                .semester(5)
                .phone("9876543211")
                .build());

        // 4. Faculty User & Profile (Dr. Raman Venkatesh)
        User facultyUser = User.builder()
                .username("faculty.ram")
                .email("ram.v@college.edu")
                .passwordHash(hash)
                .role(facultyRole)
                .status(UserStatus.ACTIVE)
                .isDeleted(false)
                .build();
        facultyUser = userRepository.save(facultyUser);

        Faculty faculty = Faculty.builder()
                .user(facultyUser)
                .facultyCode("FAC-CS-001")
                .firstName("Raman")
                .lastName("Venkatesh")
                .department("Computer Science")
                .designation("Associate Professor")
                .phone("9988776655")
                .build();
        faculty = facultyRepository.save(faculty);

        // 5. Laboratories
        Laboratory lab1 = laboratoryRepository.save(Laboratory.builder()
                .labCode("CS-LAB-01")
                .labName("Computer Networks Lab")
                .location("Block A, Floor 2, Room 201")
                .capacity(30)
                .status(LabStatus.ACTIVE)
                .description("Specialized networking hardware, routers, switches, and patch panels.")
                .build());

        Laboratory lab2 = laboratoryRepository.save(Laboratory.builder()
                .labCode("CS-LAB-02")
                .labName("Operating Systems Lab")
                .location("Block A, Floor 2, Room 202")
                .capacity(30)
                .status(LabStatus.ACTIVE)
                .description("High-performance compute clusters and embedded development boards.")
                .build());

        Laboratory lab3 = laboratoryRepository.save(Laboratory.builder()
                .labCode("EC-LAB-01")
                .labName("Electronics & Circuits Lab")
                .location("Block B, Floor 1, Room 101")
                .capacity(25)
                .status(LabStatus.ACTIVE)
                .description("Signal generators, oscilloscopes, and circuit testing equipment.")
                .build());

        // 6. Faculty Lab Assignment
        flaRepository.save(FacultyLabAssignment.builder()
                .faculty(faculty)
                .laboratory(lab1)
                .assignmentRole(AssignmentRole.INCHARGE)
                .assignedDate(LocalDate.now().minusMonths(6))
                .build());

        flaRepository.save(FacultyLabAssignment.builder()
                .faculty(faculty)
                .laboratory(lab2)
                .assignmentRole(AssignmentRole.ASSISTANT)
                .assignedDate(LocalDate.now().minusMonths(3))
                .build());

        // 7. Fair Usage Rules
        usageRuleRepository.save(UsageRule.builder()
                .laboratory(lab1)
                .maxBookingDurationHours(4)
                .maxAdvanceBookingDays(7)
                .maxConcurrentBookingsPerStudent(2)
                .allowedDays("MON,TUE,WED,THU,FRI")
                .openTime(LocalTime.of(8, 0))
                .closeTime(LocalTime.of(18, 0))
                .notes("All equipment must be inspected before and after each session.")
                .build());

        usageRuleRepository.save(UsageRule.builder()
                .laboratory(lab2)
                .maxBookingDurationHours(4)
                .maxAdvanceBookingDays(7)
                .maxConcurrentBookingsPerStudent(2)
                .allowedDays("MON,TUE,WED,THU,FRI")
                .openTime(LocalTime.of(8, 30))
                .closeTime(LocalTime.of(17, 30))
                .notes("Return all developer boards and power adapters.")
                .build());

        // 8. Registered Equipment
        Equipment eq1 = equipmentRepository.save(Equipment.builder()
                .laboratory(lab1)
                .equipmentCode("EQ-NET-001")
                .name("Cisco Catalyst 2960-X Switch")
                .category("Networking")
                .makeModel("Cisco 2960-X Series 24-Port GigE")
                .serialNumber("FCW2143A01")
                .quantityTotal(5)
                .quantityAvailable(5)
                .conditionStatus(EquipmentCondition.GOOD)
                .description("Managed 24-port Gigabit Ethernet switch for VLAN routing and network packet analysis.")
                .isDeleted(false)
                .build());

        Equipment eq2 = equipmentRepository.save(Equipment.builder()
                .laboratory(lab2)
                .equipmentCode("EQ-OS-001")
                .name("Raspberry Pi 4 Model B (8GB)")
                .category("Embedded / IoT")
                .makeModel("Raspberry Pi Foundation Rev 1.4")
                .serialNumber("RPI4-8GB-992")
                .quantityTotal(10)
                .quantityAvailable(9)
                .conditionStatus(EquipmentCondition.GOOD)
                .description("ARM Cortex-A72 quad-core 64-bit SBC with dual micro-HDMI 4K displays.")
                .isDeleted(false)
                .build());

        Equipment eq3 = equipmentRepository.save(Equipment.builder()
                .laboratory(lab2)
                .equipmentCode("EQ-OS-002")
                .name("Dell Precision Workstation i7")
                .category("Computing")
                .makeModel("Dell Precision 3640 Tower")
                .serialNumber("DELL-PREC-4421")
                .quantityTotal(15)
                .quantityAvailable(15)
                .conditionStatus(EquipmentCondition.GOOD)
                .description("Intel Core i7 10th Gen, 32GB RAM, NVIDIA Quadro P2200 for Linux kernel compiling.")
                .isDeleted(false)
                .build());

        Equipment eq4 = equipmentRepository.save(Equipment.builder()
                .laboratory(lab3)
                .equipmentCode("EQ-EC-001")
                .name("Keysight Digital Storage Oscilloscope")
                .category("Instrumentation")
                .makeModel("Keysight InfiniiVision 1000 X-Series")
                .serialNumber("KS-DSO-1022A")
                .quantityTotal(4)
                .quantityAvailable(4)
                .conditionStatus(EquipmentCondition.GOOD)
                .description("100 MHz, 2 analog channels, 2 GSa/s waveform update rate.")
                .isDeleted(false)
                .build());

        // 9. Sample Booking (Pending Approval)
        Booking sampleBooking = bookingRepository.save(Booking.builder()
                .student(student)
                .equipment(eq1)
                .bookingDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .quantityBooked(1)
                .purpose("VLAN configuration practical for Computer Networks course CS301.")
                .status(BookingStatus.PENDING)
                .build());

        // 10. Sample Notification
        notificationRepository.save(Notification.builder()
                .user(studentUser)
                .title("Welcome to College Resource Sharing")
                .message("Your student account is active. You can browse laboratory equipment and submit reservations.")
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .user(studentUser)
                .title("Booking Submitted")
                .message("Your reservation for " + eq1.getName() + " has been submitted for faculty review.")
                .type(NotificationType.BOOKING_UPDATE)
                .relatedBooking(sampleBooking)
                .isRead(false)
                .build());

        log.info("Database seed initialization completed successfully! All portals ready.");
    }
}
