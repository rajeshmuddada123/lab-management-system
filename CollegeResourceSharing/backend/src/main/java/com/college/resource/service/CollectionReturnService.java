package com.college.resource.service;

import com.college.resource.entity.*;
import com.college.resource.enums.*;
import com.college.resource.exception.*;
import com.college.resource.repository.*;
import com.college.resource.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class CollectionReturnService {

    private final BookingRepository         bookingRepository;
    private final CollectionRecordRepository collectionRepo;
    private final ReturnRecordRepository    returnRepo;
    private final EquipmentRepository       equipmentRepository;
    private final FacultyRepository         facultyRepository;
    private final FacultyLabAssignmentRepository flaRepository;
    private final FileStorageUtil           fileStorage;
    private final AuditLogger               auditLogger;
    private final NotificationService       notificationService;

    public CollectionReturnService(BookingRepository br, CollectionRecordRepository cr,
                                    ReturnRecordRepository rr, EquipmentRepository er,
                                    FacultyRepository facultyRepository,
                                    FacultyLabAssignmentRepository flaRepository,
                                    FileStorageUtil fs, AuditLogger al, NotificationService ns) {
        this.bookingRepository = br; this.collectionRepo = cr; this.returnRepo = rr;
        this.equipmentRepository = er; this.facultyRepository = facultyRepository;
        this.flaRepository = flaRepository;
        this.fileStorage = fs; this.auditLogger = al;
        this.notificationService = ns;
    }

    @Transactional
    public CollectionRecord recordCollection(Long bookingId, String condition,
                                              String notes, MultipartFile photo, User verifier) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        validateVerifierAuthorization(verifier, booking);

        if (booking.getStatus() != BookingStatus.APPROVED)
            throw new BadRequestException("Only APPROVED bookings can be collected");
        if (collectionRepo.existsByBookingBookingId(bookingId))
            throw new BadRequestException("Collection already recorded for this booking");

        FileAttachment photoFile = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                photoFile = fileStorage.store(photo, FileType.COLLECTION_PHOTO, verifier);
            } catch (IOException e) {
                throw new BadRequestException("Failed to upload collection photo: " + e.getMessage());
            }
        }

        CollectionRecord record = CollectionRecord.builder()
                .booking(booking)
                .collectedBy(booking.getStudent().getUser())
                .verifiedBy(verifier)
                .conditionOnCollection(condition != null ? condition : "GOOD")
                .notes(notes)
                .photo(photoFile)
                .build();
        collectionRepo.save(record);

        booking.setStatus(BookingStatus.COLLECTED);
        bookingRepository.save(booking);

        notificationService.notifyUser(booking.getStudent().getUser(), "Equipment Collected",
                booking.getEquipment().getName() + " has been marked as collected.",
                NotificationType.BOOKING_UPDATE, booking);
        auditLogger.log(verifier, "EQUIPMENT_COLLECTED", "bookings", bookingId,
                "Equipment collected for booking #" + bookingId);
        return record;
    }

    @Transactional
    public ReturnRecord recordReturn(Long bookingId, String condition,
                                      String notes, MultipartFile photo, User receiver) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        validateVerifierAuthorization(receiver, booking);

        if (booking.getStatus() != BookingStatus.COLLECTED && booking.getStatus() != BookingStatus.OVERDUE)
            throw new BadRequestException("Only COLLECTED or OVERDUE bookings can be returned");
        if (returnRepo.existsByBookingBookingId(bookingId))
            throw new BadRequestException("Return already recorded for this booking");

        FileAttachment photoFile = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                photoFile = fileStorage.store(photo, FileType.RETURN_PHOTO, receiver);
            } catch (IOException e) {
                throw new BadRequestException("Failed to upload return photo: " + e.getMessage());
            }
        }

        ReturnRecord record = ReturnRecord.builder()
                .booking(booking)
                .returnedBy(booking.getStudent().getUser())
                .receivedBy(receiver)
                .conditionOnReturn(condition != null ? condition : "GOOD")
                .notes(notes)
                .photo(photoFile)
                .build();
        returnRepo.save(record);

        // Restore quantity safely
        Equipment eq = booking.getEquipment();
        int restored = Math.min(eq.getQuantityTotal(), eq.getQuantityAvailable() + booking.getQuantityBooked());
        eq.setQuantityAvailable(restored);
        equipmentRepository.save(eq);

        booking.setStatus(BookingStatus.RETURNED);
        bookingRepository.save(booking);

        notificationService.notifyUser(booking.getStudent().getUser(), "Equipment Returned",
                booking.getEquipment().getName() + " has been returned successfully.",
                NotificationType.BOOKING_UPDATE, booking);
        auditLogger.log(receiver, "EQUIPMENT_RETURNED", "bookings", bookingId,
                "Equipment returned for booking #" + bookingId);
        return record;
    }

    private void validateVerifierAuthorization(User verifier, Booking booking) {
        if ("ADMIN".equals(verifier.getRole().getRoleName())) {
            return;
        }
        if ("FACULTY".equals(verifier.getRole().getRoleName())) {
            Long labId = booking.getEquipment().getLaboratory().getLabId();
            var facultyOpt = facultyRepository.findByUserUserId(verifier.getUserId());
            if (facultyOpt.isPresent()) {
                boolean assigned = flaRepository.findActiveByFacultyId(facultyOpt.get().getFacultyId())
                        .stream().anyMatch(a -> a.getLaboratory().getLabId().equals(labId));
                if (assigned) return;
            }
            throw new UnauthorisedException("You are not assigned to this equipment's laboratory");
        }
        throw new UnauthorisedException("Only Admin or assigned Faculty can verify collection or return");
    }
}
