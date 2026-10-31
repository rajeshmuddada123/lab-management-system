package com.college.resource.service;

import com.college.resource.entity.*;
import com.college.resource.enums.EquipmentCondition;
import com.college.resource.enums.FileType;
import com.college.resource.enums.LabStatus;
import com.college.resource.exception.*;
import com.college.resource.repository.*;
import com.college.resource.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository       equipmentRepository;
    private final EquipmentImageRepository  imageRepository;
    private final LaboratoryRepository      labRepository;
    private final FacultyLabAssignmentRepository flaRepository;
    private final FacultyRepository         facultyRepository;
    private final FileStorageUtil           fileStorage;
    private final AuditLogger              auditLogger;

    public EquipmentService(EquipmentRepository er, EquipmentImageRepository ir,
                             LaboratoryRepository lr, FacultyLabAssignmentRepository flar,
                             FacultyRepository facultyRepository,
                             FileStorageUtil fs, AuditLogger al) {
        this.equipmentRepository = er; this.imageRepository = ir; this.labRepository = lr;
        this.flaRepository = flar; this.facultyRepository = facultyRepository;
        this.fileStorage = fs; this.auditLogger = al;
    }

    public Page<Equipment> browseEquipment(String keyword, String category, Boolean available, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) return equipmentRepository.searchEquipment(keyword, pageable);
        if (available != null && available)         return equipmentRepository.findAvailable(pageable);
        if (category  != null && !category.isBlank()) return equipmentRepository.findByCategoryAndIsDeletedFalse(category, pageable);
        return equipmentRepository.findByIsDeletedFalse(pageable);
    }

    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .filter(e -> !e.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id));
    }

    public Page<Equipment> getEquipmentByLab(Long labId, String category, Boolean available, Pageable pageable) {
        return equipmentRepository.findByLabWithFilters(labId, category, available, pageable);
    }

    public List<Laboratory> getActiveLabs() {
        return labRepository.findByStatus(LabStatus.ACTIVE);
    }

    public Laboratory getLabById(Long labId) {
        return labRepository.findById(labId)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory", labId));
    }

    public List<Laboratory> getFacultyLabs(User facultyUser) {
        var facultyOpt = facultyRepository.findByUserUserId(facultyUser.getUserId());
        if (facultyOpt.isEmpty()) {
            return List.of();
        }
        Long facultyId = facultyOpt.get().getFacultyId();
        return flaRepository.findActiveByFacultyId(facultyId).stream()
                .map(FacultyLabAssignment::getLaboratory).toList();
    }

    @Transactional
    public EquipmentImage uploadEquipmentImage(Long equipmentId, MultipartFile file,
                                                boolean isPrimary, User uploader) {
        Equipment eq = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId));
        try {
            FileAttachment attachment = fileStorage.store(file, FileType.EQUIPMENT_IMAGE, uploader);
            List<EquipmentImage> existing = imageRepository
                    .findByEquipmentEquipmentIdOrderByDisplayOrderAsc(equipmentId);

            // Ensure only one primary image
            if (isPrimary) {
                existing.forEach(img -> { img.setIsPrimary(false); imageRepository.save(img); });
            }

            EquipmentImage image = EquipmentImage.builder()
                    .equipment(eq).file(attachment)
                    .isPrimary(existing.isEmpty() || isPrimary)
                    .displayOrder(existing.size())
                    .build();
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new BadRequestException("Image upload failed: " + e.getMessage());
        }
    }

    public List<EquipmentImage> getEquipmentImages(Long equipmentId) {
        return imageRepository.findByEquipmentEquipmentIdOrderByDisplayOrderAsc(equipmentId);
    }

    @Transactional
    public void updateEquipmentAvailability(Long equipmentId, int availableQty, String condition, User faculty) {
        Equipment eq = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId));
        if (availableQty > eq.getQuantityTotal())
            throw new BadRequestException("Available quantity cannot exceed total quantity");
        eq.setQuantityAvailable(availableQty);
        if (condition != null) eq.setConditionStatus(EquipmentCondition.valueOf(condition));
        equipmentRepository.save(eq);
        auditLogger.log(faculty, "EQUIPMENT_AVAILABILITY_UPDATED", "equipment", equipmentId,
                "Availability updated to " + availableQty);
    }
}
