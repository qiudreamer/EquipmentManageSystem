package com.example.springboot_ch_1.entity.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/27
 * Time: 2:07
 * Description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceChangeMsg {
    private String equipmentId;
    private String equipmentName;
    private String equipmentCode;
    private String equipmentTag;
    private String equipmentImg;
}
