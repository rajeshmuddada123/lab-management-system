package com.college.resource.repository;

import com.college.resource.entity.EquipmentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipmentImageRepository extends JpaRepository<EquipmentImage, Long> {
    List<EquipmentImage> findByEquipmentEquipmentIdOrderByDisplayOrderAsc(Long equipmentId);
    void deleteByEquipmentEquipmentIdAndFileFileId(Long equipmentId, Long fileId);
}
