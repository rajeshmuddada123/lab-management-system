package com.college.resource.service;

import com.college.resource.dto.request.BookingRequest;
import com.college.resource.entity.*;
import com.college.resource.enums.*;
import com.college.resource.exception.*;
import com.college.resource.repository.*;
import com.college.resource.util.AuditLogger;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository      bookingRepository;
    private final EquipmentRepository    equipmentRepository;
    private final StudentRepository      studentRepository;
    private final FacultyRepository      facultyRepository;
    private final FacultyLabAssignmentRepository flaRepository;
    private final BookingApprovalRepository approvalRepository;
    private final WaitingListRepository  waitlistRepository;
    private final UsageRuleRepository    usageRuleRepository;
    private final NotificationService    notificationService;
    private final AuditLogger            auditLogger;

    public BookingService(BookingRepository br, EquipmentRepository er, StudentRepository sr,
                          FacultyRepository facultyRepository,
                          FacultyLabAssignmentRepository flar, BookingApprovalRepository bar,
                          WaitingListRepository wlr, UsageRuleRepository urr,
                          NotificationService ns, AuditLogger al) {
        this.bookingRepository = br; this.equipmentRepository = er; this.studentRepository = sr;
        this.facultyRepository = facultyRepository;
        this.flaRepository = flar; this.approvalRepository = bar; this.waitlistRepository = wlr;
        this.usageRuleRepository = urr; this.notificationService = ns; this.auditLogger = al;
    }

    // ── CREATE BOOKING ────────────────────────────────────────

    @Transactional
    public Booking createBooking(BookingRequest req, User currentUser) {
        Student student = studentRepository.findByUserUserId(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Equipment eq = equipmentRepository.findById(req.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", req.getEquipmentId()));

        if (eq.getIsDeleted()) throw new BadRequestException("Equipment is not available");

        // Validate time
        if (!req.getEndTime().isAfter(req.getStartTime()))
            throw new BadRequestException("End time must be after start time");

        // Validate advance booking days
        UsageRule rule = usageRuleRepository.findByLaboratoryLabId(eq.getLaboratory().getLabId())
                .orElse(null);
        if (rule != null) {
            long daysDiff = ChronoUnit.DAYS.between(LocalDate.now(), req.getBookingDate());
            if (daysDiff > rule.getMaxAdvanceBookingDays())
                throw new BadRequestException("Cannot book more than " + rule.getMaxAdvanceBookingDays() + " days in advance");
            if (daysDiff < 0)
                throw new BadRequestException("Cannot book for a past date");

            Duration duration = Duration.between(req.getStartTime(), req.getEndTime());
            if (duration.toHours() > rule.getMaxBookingDurationHours())
                throw new BadRequestException("Booking duration exceeds maximum of " + rule.getMaxBookingDurationHours() + " hours");

            // Concurrent booking check
            long active = bookingRepository.countActiveBookingsForStudent(student.getStudentId());
            if (active >= rule.getMaxConcurrentBookingsPerStudent())
                throw new BadRequestException("You have reached the maximum concurrent bookings limit");
        }

        // Quantity check
        if (req.getQuantityBooked() <= 0) {
            throw new BadRequestException("Quantity booked must be at least 1");
        }
        if (req.getQuantityBooked() > eq.getQuantityAvailable()) {
            // No quantity available → add to waitlist
            addToWaitlist(eq, student, req);
            throw new BadRequestException("Equipment not available. You have been added to the waiting list.");
        }

        // Conflict check (time overlap)
        long conflicts = bookingRepository.countConflicts(
                req.getEquipmentId(), req.getBookingDate(), req.getStartTime(), req.getEndTime());
        if (conflicts > 0) {
            addToWaitlist(eq, student, req);
            throw new BadRequestException("This time slot is already booked. You have been added to the waiting list.");
        }

        Booking booking = Booking.builder()
                .student(student).equipment(eq)
                .purpose(req.getPurpose()).bookingDate(req.getBookingDate())
                .startTime(req.getStartTime()).endTime(req.getEndTime())
                .quantityBooked(req.getQuantityBooked()).status(BookingStatus.PENDING)
                .build();
        booking = bookingRepository.save(booking);

        // Update available quantity (reserved)
        int remaining = Math.max(0, eq.getQuantityAvailable() - req.getQuantityBooked());
        eq.setQuantityAvailable(remaining);
        equipmentRepository.save(eq);

        notificationService.notifyUser(currentUser, "Booking Submitted",
                "Your booking for " + eq.getName() + " has been submitted and is pending approval.",
                NotificationType.BOOKING_UPDATE, booking);

        auditLogger.log(currentUser, "BOOKING_CREATED", "bookings", booking.getBookingId(),
                "Created booking for: " + eq.getEquipmentCode());
        return booking;
    }

    private void addToWaitlist(Equipment eq, Student student, BookingRequest req) {
        Integer maxPos = waitlistRepository.findMaxQueuePosition(
                eq.getEquipmentId(), req.getBookingDate(), req.getStartTime(), req.getEndTime());
        int nextPos = (maxPos == null) ? 1 : maxPos + 1;
        WaitingList wl = WaitingList.builder()
                .equipment(eq).student(student)
                .bookingDate(req.getBookingDate())
                .startTime(req.getStartTime()).endTime(req.getEndTime())
                .queuePosition(nextPos).status(WaitlistStatus.WAITING).build();
        waitlistRepository.save(wl);
        notificationService.notifyUser(student.getUser(), "Added to Waitlist",
                "You are #" + nextPos + " in the waiting list for " + eq.getName(),
                NotificationType.WAITLIST, null);
    }

    // ── APPROVE / REJECT ─────────────────────────────────────

    @Transactional
    public Booking approveBooking(Long bookingId, ApprovalAction action, String notes, User approver) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (booking.getStatus() != BookingStatus.PENDING)
            throw new BadRequestException("Only PENDING bookings can be approved/rejected");

        // Faculty can only approve bookings for their labs
        if ("FACULTY".equals(approver.getRole().getRoleName())) {
            Long targetLabId = booking.getEquipment().getLaboratory().getLabId();
            Long facultyId = getFacultyId(approver);
            var fac = flaRepository.findActiveByFacultyId(facultyId);
            boolean assigned = fac.stream().anyMatch(a ->
                    a.getLaboratory().getLabId().equals(targetLabId));
            if (!assigned)
                throw new UnauthorisedException("You are not assigned to this equipment's laboratory");
        } else if (!"ADMIN".equals(approver.getRole().getRoleName())) {
            throw new UnauthorisedException("Only Admin or assigned Faculty can approve/reject bookings");
        }

        BookingApproval approval = BookingApproval.builder()
                .booking(booking).approvedBy(approver).action(action).notes(notes).build();
        approvalRepository.save(approval);

        if (action == ApprovalAction.APPROVED) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            booking.setRejectionReason(notes);
            // Restore quantity safely
            Equipment eq = booking.getEquipment();
            int newQty = Math.min(eq.getQuantityTotal(), eq.getQuantityAvailable() + booking.getQuantityBooked());
            eq.setQuantityAvailable(newQty);
            equipmentRepository.save(eq);
            promoteWaitlist(booking);
        }
        booking = bookingRepository.save(booking);

        String notifMsg = action == ApprovalAction.APPROVED
                ? "Your booking for " + booking.getEquipment().getName() + " has been APPROVED."
                : "Your booking for " + booking.getEquipment().getName() + " has been REJECTED. Reason: " + notes;
        notificationService.notifyUser(booking.getStudent().getUser(), "Booking " + action.name(), notifMsg,
                NotificationType.APPROVAL, booking);

        auditLogger.log(approver, "BOOKING_" + action.name(), "bookings", bookingId,
                action.name() + " booking #" + bookingId);
        return booking;
    }

    // ── CANCEL ───────────────────────────────────────────────

    @Transactional
    public Booking cancelBooking(Long bookingId, User currentUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        if (!booking.getStudent().getUser().getUserId().equals(currentUser.getUserId())
                && !"ADMIN".equals(currentUser.getRole().getRoleName()))
            throw new UnauthorisedException("Cannot cancel someone else's booking");
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.REJECTED)
            throw new BadRequestException("Booking is already " + booking.getStatus());
        if (booking.getStatus() == BookingStatus.COLLECTED || booking.getStatus() == BookingStatus.RETURNED)
            throw new BadRequestException("Cannot cancel a collected/returned booking");

        booking.setStatus(BookingStatus.CANCELLED);
        Equipment eq = booking.getEquipment();
        int newQty = Math.min(eq.getQuantityTotal(), eq.getQuantityAvailable() + booking.getQuantityBooked());
        eq.setQuantityAvailable(newQty);
        equipmentRepository.save(eq);
        promoteWaitlist(booking);
        auditLogger.log(currentUser, "BOOKING_CANCELLED", "bookings", bookingId, "Booking cancelled");
        return bookingRepository.save(booking);
    }

    // ── WAITLIST PROMOTION ────────────────────────────────────

    private void promoteWaitlist(Booking cancelledBooking) {
        List<WaitingList> waitlist = waitlistRepository
                .findByEquipmentEquipmentIdAndBookingDateAndStartTimeAndEndTimeAndStatus(
                        cancelledBooking.getEquipment().getEquipmentId(),
                        cancelledBooking.getBookingDate(),
                        cancelledBooking.getStartTime(),
                        cancelledBooking.getEndTime(),
                        WaitlistStatus.WAITING);
        if (!waitlist.isEmpty()) {
            WaitingList first = waitlist.get(0);
            first.setStatus(WaitlistStatus.PROMOTED);
            waitlistRepository.save(first);
            notificationService.notifyUser(first.getStudent().getUser(),
                    "Waitlist Promotion",
                    "A slot is now available for " + cancelledBooking.getEquipment().getName() +
                    " on " + cancelledBooking.getBookingDate() + ". Please submit a booking.",
                    NotificationType.WAITLIST, null);
        }
    }

    // ── OVERDUE DETECTION (runs daily at 1 AM) ────────────────

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void markOverdueBookings() {
        List<Booking> overdue = bookingRepository.findOverdue(LocalDate.now());
        overdue.forEach(b -> {
            b.setStatus(BookingStatus.OVERDUE);
            bookingRepository.save(b);
            notificationService.notifyUser(b.getStudent().getUser(), "Equipment Overdue",
                    "Equipment " + b.getEquipment().getName() + " booked on " + b.getBookingDate() +
                    " is overdue. Please return it immediately.",
                    NotificationType.REMINDER, b);
        });
    }

    // ── QUERIES ───────────────────────────────────────────────

    public Page<Booking> getStudentBookings(Long studentId, BookingStatus status, Pageable pageable) {
        if (status != null) return bookingRepository.findByStudentStudentIdAndStatus(studentId, status, pageable);
        return bookingRepository.findByStudentStudentId(studentId, pageable);
    }

    public Page<Booking> getBookingsForFaculty(User facultyUser, Pageable pageable) {
        Long facultyId = getFacultyId(facultyUser);
        List<Long> labIds = flaRepository.findActiveByFacultyId(facultyId)
                .stream().map(a -> a.getLaboratory().getLabId()).toList();
        if (labIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return bookingRepository.findAllForLabs(labIds, pageable);
    }

    public Page<Booking> getPendingBookingsForFaculty(User facultyUser, Pageable pageable) {
        Long facultyId = getFacultyId(facultyUser);
        List<Long> labIds = flaRepository.findActiveByFacultyId(facultyId)
                .stream().map(a -> a.getLaboratory().getLabId()).toList();
        if (labIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return bookingRepository.findPendingForLabs(labIds, pageable);
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
    }

    private Long getFacultyId(User user) {
        return facultyRepository.findByUserUserId(user.getUserId())
                .map(Faculty::getFacultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found for user: " + user.getUsername()));
    }
}
