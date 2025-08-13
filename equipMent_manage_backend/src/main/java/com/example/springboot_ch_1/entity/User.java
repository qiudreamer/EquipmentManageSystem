package com.example.springboot_ch_1.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:40
 * Description:
 */
@Entity
@Data
public class User {
    @Id
//  用户账号（英文拼音+学号后四位）
    private String userAccount;
//  用户昵称(实名)
    private String userName;
//  用户密码
    private String password;
//  用户登录时间
    private String loginTime;
//用户权限
    public String rootType;
}