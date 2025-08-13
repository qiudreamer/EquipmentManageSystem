package com.example.springboot_ch_1.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:41
 * Description:
 */
@Entity
@Data
public class Device {
    @Id
//  设备id
    private String equipmentId;
//  设备名称
    private String equipmentName;
//  设备编号
    private String equipmentCode;
//  设备图片
    private String equipmentImg;
    @Column(length = 500)
//  设备介绍
    private String equipmentDesc;
    //  设备状态
    @Enumerated(EnumType.STRING)
    private Status equipmentStatus;
    //  设备上下架状态（out是下架，on是已上架）
    @Enumerated(EnumType.STRING)
    private OnOrOutStatus equipmentOutOrOnStatus;
//  设备标签
    private String equipmentTag;
//  设备创建时间
    private String equipmentCreateTime;
//  设备状态类型：可借出，已借出

    public enum Status { available, borrowed}
    public enum OnOrOutStatus{ out, on }
}
