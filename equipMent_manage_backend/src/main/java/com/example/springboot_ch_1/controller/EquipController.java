package com.example.springboot_ch_1.controller;

import cn.hutool.json.JSONObject;
import com.example.springboot_ch_1.entity.ReturnStatus;
import com.example.springboot_ch_1.service.device.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:07
 * Description:
 */
@RestController
@RequestMapping("/equipment")
public class EquipController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/getAllEquipment")
    public ReturnStatus getAllEquipment(@RequestBody String data) {
        return deviceService.getAllEquipment(data);
    }

    @PostMapping("/getWaitReturnEquipment")
    public ReturnStatus getWaitReturnEquipment(@RequestBody String data) {
        return deviceService.getWaitReturnEquipment(data);
    }

    @PostMapping("/getDetailEquipmentData")
    public ReturnStatus getDetailEquipmentData(@RequestBody String data) {
        return deviceService.getDetailEquipmentData(data);
    }

    @PostMapping("/searchEquipment")
    public ReturnStatus searchEquipment(@RequestBody String data){
        return deviceService.searchEquipment(data);
    }

    @PostMapping("/submitNewEquipmentData")
    public ReturnStatus submitNewEquipmentData(@RequestBody String data){
        return deviceService.submitNewEquipmentData(data);
    }
    @PostMapping("/outEquipment")
    public ReturnStatus outEquipment(@RequestBody String data){
        return deviceService.outEquipment(data);
    }
    @PostMapping("/onEquipment")
    public ReturnStatus onEquipment(@RequestBody String data){
        return deviceService.onEquipment(data);
    }
    @PostMapping("/doDeleteEquipment")
    public ReturnStatus doDeleteEquipment(@RequestBody String data){
        return deviceService.doDeleteEquipment(data);
    }
    @PostMapping("/submitEditEquipmentData")
    public ReturnStatus submitEditEquipmentData(@RequestBody String data){
        return deviceService.submitEditEquipmentData(data);
    }
    @PostMapping("/getAllCheckEquipment")
    private ReturnStatus getAllCheckEquipment(@RequestBody String data){
        return deviceService.getAllCheckEquipment(data);
    }

    @PostMapping("/revokeEquipmentCheck")
    private ReturnStatus revokeEquipmentCheck(@RequestBody String data){
        return deviceService.revokeEquipmentCheck(data);
    }

    @PostMapping("/agreeEquipmentCheck")
    private ReturnStatus agreeEquipmentCheck(@RequestBody String data){
        return deviceService.agreeEquipmentCheck(data);
    }

}