package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.BorrowAndReturnOrder;
import com.example.springboot_ch_1.entity.Device;
import org.springframework.data.domain.PageRequest;
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

    BorrowAndReturnOrder findByOrderId(String orderId);
    BorrowAndReturnOrder findByDeviceIdAndUserIdAndOrderTypeAndOpType(String deviceId, String userId,  BorrowAndReturnOrder.OrderType orderType, String opType);
    long countBorrowAndReturnOrderByUserId(String userAccount);

    List<BorrowAndReturnOrder> findAllByOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(PageRequest pageRequest);

    List<BorrowAndReturnOrder> findAllByDeviceId(String equipmentId);


//    登神长阶
    List<BorrowAndReturnOrder> findAllByOpTypeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(String opType, PageRequest pageRequest);
    long countByOpType(String opType);


    List<BorrowAndReturnOrder> findAllByUserNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(String userName, PageRequest pageRequest);
    long countByUserNameLike(String userName);


    List<BorrowAndReturnOrder> findAllByOpTypeAndUserNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(String opType, String userName, PageRequest pageRequest);
    long countByOpTypeAndUserNameLike(String opType,String userName);


    List<BorrowAndReturnOrder> findAllByDeviceNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(String equipmentName, PageRequest pageRequest);
    long countByDeviceNameLike(String userName);


    List<BorrowAndReturnOrder> findAllByOpTypeAndDeviceNameLikeOrderByOrderTypeAscOpTypeDescBorrowTimeDesc(String opType, String deviceName, PageRequest pageRequest);
    long countByOpTypeAndDeviceNameLike(String opType,String deviceName);
}
