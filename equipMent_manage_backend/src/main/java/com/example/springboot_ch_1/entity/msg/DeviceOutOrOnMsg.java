package com.example.springboot_ch_1.entity.msg;

import com.example.springboot_ch_1.entity.Device;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/26
 * Time: 22:38
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceOutOrOnMsg {
    private String equipmentId;
    private Device.OnOrOutStatus onOrOutStatus;
}
