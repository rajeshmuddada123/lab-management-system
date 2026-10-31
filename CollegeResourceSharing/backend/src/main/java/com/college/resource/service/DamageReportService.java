package com.college.resource.service;

import com.college.resource.dto.request.DamageReportRequest;
import com.college.resource.entity.*;
import com.college.resource.enums.*;
import com.college.resource.exception.ResourceNotFoundException;
import com.college.resource.repository.*;
import com.college.resource.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DamageReportService {

    private final DamageReportRepository damageReportRepository;
    private final EquipmentRepository    equipmentRepository;
    private final BookingRepository      bookingRepository;
    private final ConditionReportRepository conditionReportRepository;
    private final FileStorageUtil        fileStorage;
    private final AuditLogger            auditLogger;
    private final NotificationService    notificationService;

    public DamageReportService(DamageReportRepository drr, EquipmentRepository er,
                                BookingRepository br, ConditionReportRepository crr,
                                FileStorageUtil fs, AuditLogger al, NotificationService ns) {
        this.damageReportRepository = drr; this.equipmentRepository = er; this.bookingRepository = br;
        this.conditionReportRepository = crr; this.fileStorage = fs; this.auditLogger = al;
        this.notificationService = ns;
    }

    @Transactional
    public DamageReport reportDamage(DamageReportRequest req, MultipartFile photo, User reporter) {
        Equipment eq = equipmentRepository.findById(req.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", req.getEquipmentId()));

        Booking booking = null;
        if (req.getBookingId() != null) {
            booking = bookingRepository.findById(req.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking", req.getBookingId()));
        }

        FileAttachment photoFile = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                photoFile = fileStorage.store(photo, FileType.DAMAGE_PHOTO, reporter);
            } catch (IOException e) {
                throw new com.college.resource.exception.BadRequestException(
                        "Failed to upload damage photo: " + e.getMessage());
            }
        }

        DamageReport dr = DamageReport.builder()
                .equipment(eq).booking(booking).reportedBy(reporter)
                .severity(req.getSeverity()).description(req.getDescription())
                .repairCost(req.getRepairCost()).photo(photoFile).build();
        dr = damageReportRepository.save(dr);

        auditLogger.log(reporter, "DAMAGE_REPORTED", "damage_reports", dr.getDamageId(),
                "Damage reported for equipment: " + eq.getEquipmentCode());
        return dr;
    }

    public Page<DamageReport> getDamageReports(Long equipmentId, DamageStatus status, Pageable pageable) {
        if (equipmentId != null) return damageReportRepository.findByEquipmentEquipmentId(equipmentId, pageable);
        if (status != null) return damageReportRepository.findByStatus(status, pageable);
        return damageReportRepository.findAll(pageable);
    }

    @Transactional
    public ConditionReport submitConditionReport(Long equipmentId, String conditionStatus,
                                                  String description, MultipartFile photo, User reporter) {
        Equipment eq = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId));

        FileAttachment photoFile = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                photoFile = fileStorage.store(photo, FileType.DAMAGE_PHOTO, reporter);
            } catch (IOException e) {
                throw new com.college.resource.exception.BadRequestException("Photo upload failed");
            }
        }

        ConditionReport cr = ConditionReport.builder()
                .equipment(eq).reportedBy(reporter)
                .conditionStatus(ConditionStatus.valueOf(conditionStatus))
                .description(description)
                .reportDate(java.time.LocalDate.now())
                .photo(photoFile).build();
        return conditionReportRepository.save(cr);
    }
}
