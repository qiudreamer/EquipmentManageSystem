package com.example.springboot_ch_1.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/8/11
 * Time: 15:36
 * Description:
 */
@Entity
@Data
public class BorrowEquipmentCheck {
    @Id
    private String checkId;
    private String equipmentId;
    private String userId;
    private String equipmentReason;
    private String checkTime;
}
