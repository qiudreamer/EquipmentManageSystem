package com.example.springboot_ch_1.entity;

import lombok.Data;
import org.hibernate.type.ListType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:41
 * Description:
 */
@Entity
@Data
public class BorrowAndReturnOrder {
    @Id
//  工单id
    private String orderId;
//  操作人id
    private String userId;
//  操作人名称
    private String userName;
//  操作设备id
    private String deviceId;
//  操作设备编号
    private String deviceCode;
//  操作设备名称
    private String deviceName;
//  借出时间
    private String borrowTime;
//  归还时间
    private String returnTime;
//  工单状态
    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    public enum OrderType { Continue, Finish, Kill}

//  工单类型(借还类工单，设备上报类工单，设备上下架类工单）
    private String opType;

}