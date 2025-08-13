package com.example.springboot_ch_1.entity.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/27
 * Time: 15:38
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStatusMsg {
    private String userAccount;
}
