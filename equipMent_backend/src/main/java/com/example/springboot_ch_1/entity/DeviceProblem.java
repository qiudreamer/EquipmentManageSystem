package com.example.springboot_ch_1.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/25
 * Time: 15:43
 * Description:
 */
@Entity
@Data
public class DeviceProblem {
    @Id
    String problemId;
    String userAccount;
    String equipmentId;
    String reasons;
    String detailedReason;
    String orderId;
}
