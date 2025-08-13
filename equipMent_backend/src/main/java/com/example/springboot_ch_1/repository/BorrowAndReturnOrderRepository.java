package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.BorrowAndReturnOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:43
 * Description:
 */
public interface BorrowAndReturnOrderRepository extends JpaRepository<BorrowAndReturnOrder, String>, JpaSpecificationExecutor {

    List<BorrowAndReturnOrder> findAllByUserIdOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(String userAccount, Pageable pageable);

    List<BorrowAndReturnOrder> findAllByDeviceIdAndUserIdAndOrderTypeAndOpType(String deviceId, String userAccount, BorrowAndReturnOrder.OrderType equipmentStatus,String opType);
    BorrowAndReturnOrder findByDeviceIdAndOrderType(String deviceId, BorrowAndReturnOrder.OrderType equipmentStatus);

    long countBorrowAndReturnOrderByUserId(String userAccount);

//    void deleteBorrowAndReturnOrderByOrderId(String orderId);
}
