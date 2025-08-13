package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.BorrowAndReturnOrder;
import com.example.springboot_ch_1.entity.DeviceBorrow;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.awt.print.Pageable;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 21:31
 * Description:
 */
public interface DeviceBorrowRepository extends JpaRepository<DeviceBorrow, String>, JpaSpecificationExecutor {
    List<DeviceBorrow> findAllByDeviceBorrowUserId(String deviceBorrowUserId, PageRequest pageRequest);

    long countByDeviceBorrowUserId(String deviceBorrowUserId);
    void deleteByDeviceBorrowDeviceIdAndDeviceBorrowUserId(String deviceBorrowDeviceId, String deviceBorrowUserId);
}
