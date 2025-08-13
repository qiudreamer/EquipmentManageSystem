package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:57
 * Description:
 */
public interface DeviceRepository extends JpaRepository<Device, String>, JpaSpecificationExecutor {
    List<Device> findAllByOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(Pageable pageable);

    Device findByEquipmentId(String equipmentId);

    List<Device> findAllByEquipmentNameLikeOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(String equipmentName, PageRequest pageRequest);
    List<Device> findAllByEquipmentCodeLikeOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(String equipmentCode, PageRequest pageRequest);
    List<Device> findAllByEquipmentTagLikeOrderByEquipmentOutOrOnStatusAscEquipmentCreateTimeDesc(String equipmentTag, PageRequest pageRequest);
    long countByEquipmentNameLikeOrderByEquipmentCreateTimeDesc(String equipmentName);
    long countByEquipmentCodeLikeOrderByEquipmentCreateTimeDesc(String equipmentName);
    long countByEquipmentTagLikeOrderByEquipmentCreateTimeDesc(String equipmentName);

}

