package com.example.springboot_ch_1.service.device;

import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.entity.ReturnStatus;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:05
 * Description:
 */
public interface DeviceService {
    ReturnStatus getAllEquipment(String data);

    ReturnStatus borrowEquipment(String data);

    ReturnStatus returnEquipment(String data);

    ReturnStatus getWaitReturnEquipment(String data);

    ReturnStatus getDetailEquipmentData(String data);

    ReturnStatus homeNervousNeed(String data);

    ReturnStatus setDialogAboutEquipment(String data);

    ReturnStatus searchEquipment(String data);

    ReturnStatus borrowEquipmentRequest(String data);

    ReturnStatus getAllCheckEquipment(String data);

    ReturnStatus revokeEquipmentCheck(String data);
}
