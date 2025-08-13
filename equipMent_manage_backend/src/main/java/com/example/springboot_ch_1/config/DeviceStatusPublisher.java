package com.example.springboot_ch_1.config;

import com.example.springboot_ch_1.entity.Device;
import com.example.springboot_ch_1.entity.msg.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:55
 * Description:
 */
@Component
@RequiredArgsConstructor
public class DeviceStatusPublisher {
    private final RedisTemplate<String, Object> redis;

    public void publish(String deviceId, Device.OnOrOutStatus status) {
        System.out.println("Publishing message: " + new DeviceOutOrOnMsg(deviceId, status));
        redis.convertAndSend("device.status.manage", new DeviceOutOrOnMsg(deviceId, status));
    }

    public void publishDeleteEquipment(String deviceId) {
        System.out.println("Publishing message: " + new DeviceDeleteMsg(deviceId));
        redis.convertAndSend("device.delete.manage", new DeviceDeleteMsg(deviceId));
    }

    public void publishChangeEquipment(String deviceId,String equipmentName,String equipmentCode,String equipmentTag,String equipmentImg) {
        System.out.println("Publishing message: " + new DeviceChangeMsg(deviceId,equipmentName,equipmentCode,equipmentTag,equipmentImg));
        redis.convertAndSend("device.change.manage", new DeviceChangeMsg(deviceId,equipmentName,equipmentCode,equipmentTag,equipmentImg));
    }

    public void publishStewardStatus(String userAccount) {
        System.out.println("Publishing message: " + new StewardStatusMsg(userAccount));
        redis.convertAndSend("user.steward.manage", new StewardStatusMsg(userAccount));
    }

    public void publishUserStatus(String userAccount) {
        System.out.println("Publishing message: " + new UserStatusMsg(userAccount));
        redis.convertAndSend("user.account.manage", new UserStatusMsg(userAccount));
    }

    public void publishBorrowBorrowStatus(String deviceId, Device.Status status) {
        System.out.println("Publishing message: " + new DeviceStatusMsg(deviceId, status));
        redis.convertAndSend("device.status", new DeviceStatusMsg(deviceId, status));
    }

}

