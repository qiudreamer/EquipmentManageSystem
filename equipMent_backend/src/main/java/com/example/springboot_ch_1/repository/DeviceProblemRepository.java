package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.DeviceProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/25
 * Time: 15:45
 * Description:
 */
public interface DeviceProblemRepository  extends JpaRepository<DeviceProblem, String>, JpaSpecificationExecutor {
    DeviceProblem findByUserAccountAndEquipmentId(String userAccount,String equipmentId);

    void deleteByUserAccountAndEquipmentId(String userAccount,String equipmentId);
}
