package com.example.springboot_ch_1.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/26
 * Time: 5:20
 * Description:
 */
@Entity
@Data
public class UserRoot {
    @Id
    String userAccount;
    String userPassword;
    String rootType;
    String userName;
    String loginTime;
}
