package com.example.springboot_ch_1.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 21:28
 * Description:
 */
@Entity
@Data
public class DeviceBorrow {
    @Id
    //  借出设备Id
    private String deviceBorrowDeviceId;
//  借出人Id
    private String deviceBorrowUserId;
//  对应工单Id
    private String orderId;
}
