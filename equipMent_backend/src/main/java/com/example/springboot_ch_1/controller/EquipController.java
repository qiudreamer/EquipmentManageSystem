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

    @PostMapping("/borrowEquipment")
    public ReturnStatus borrowEquipment(@RequestBody String data) {
        return deviceService.borrowEquipment(data);
    }

    @PostMapping("/returnEquipment")
    public ReturnStatus returnEquipment(@RequestBody String data) {
        return deviceService.returnEquipment(data);
    }

    @PostMapping("/getWaitReturnEquipment")
    public ReturnStatus getWaitReturnEquipment(@RequestBody String data) {
        return deviceService.getWaitReturnEquipment(data);
    }

    @PostMapping("/getDetailEquipmentData")
    public ReturnStatus getDetailEquipmentData(@RequestBody String data) {
        return deviceService.getDetailEquipmentData(data);
    }

    @PostMapping("/homeNervousNeed")
    public ReturnStatus homeNervousNeed(@RequestBody String data) {
        return deviceService.homeNervousNeed(data);
    }

    @PostMapping("/setDialogAboutEquipment")
    public ReturnStatus setDialogAboutEquipment(@RequestBody String data){
        return deviceService.setDialogAboutEquipment(data);
    }
    @PostMapping("/searchEquipment")
    public ReturnStatus searchEquipment(@RequestBody String data){
        return deviceService.searchEquipment(data);
    }

    @PostMapping("/borrowEquipmentRequest")
    public ReturnStatus borrowEquipmentRequest(@RequestBody String data){
        return deviceService.borrowEquipmentRequest(data);
    }

    @PostMapping("/getAllCheckEquipment")
    private ReturnStatus getAllCheckEquipment(@RequestBody String data){
        return deviceService.getAllCheckEquipment(data);
    }

    @PostMapping("/revokeEquipmentCheck")
    private ReturnStatus revokeEquipmentCheck(@RequestBody String data){
        return deviceService.revokeEquipmentCheck(data);
    }

}