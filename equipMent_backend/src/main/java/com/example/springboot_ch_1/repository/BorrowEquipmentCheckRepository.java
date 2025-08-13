package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.BorrowEquipmentCheck;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/8/11
 * Time: 15:38
 * Description:
 */
public interface BorrowEquipmentCheckRepository extends JpaRepository<BorrowEquipmentCheck,String>, JpaSpecificationExecutor {
    BorrowEquipmentCheck findByUserIdAndEquipmentId(String userId,String equipmentId);
    BorrowEquipmentCheck findByCheckId(String checkId);

    List<BorrowEquipmentCheck> findAllByOrderByCheckTimeDesc(PageRequest pageRequest);

}
