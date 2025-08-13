package com.example.springboot_ch_1.entity;

import lombok.Data;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/8/11
 * Time: 17:16
 * Description:
 */

@Data
public class BorrowEquipmentCheckDTO {
    private String checkId;
    private String equipmentImg;
    private String equipmentName;
    private String equipmentCode;
    private String equipmentCheckTime;
    private String equipmentReason;
}
