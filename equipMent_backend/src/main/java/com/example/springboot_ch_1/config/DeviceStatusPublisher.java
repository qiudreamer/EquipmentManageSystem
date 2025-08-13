package com.example.springboot_ch_1.config;

import com.example.springboot_ch_1.entity.Device;
import com.example.springboot_ch_1.entity.DeviceStatusMsg;
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

    public void publish(String deviceId, Device.Status status) {
        System.out.println("Publishing message: " + new DeviceStatusMsg(deviceId, status));
        redis.convertAndSend("device.status", new DeviceStatusMsg(deviceId, status));
    }

}

