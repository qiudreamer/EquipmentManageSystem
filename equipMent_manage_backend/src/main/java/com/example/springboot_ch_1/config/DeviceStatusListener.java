package com.example.springboot_ch_1.config;

import com.example.springboot_ch_1.entity.msg.DeviceOutOrOnMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DeviceStatusListener implements MessageListener {

    @Autowired
    private SimpMessagingTemplate ws;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            DeviceOutOrOnMsg msg = mapper.readValue(message.getBody(), DeviceOutOrOnMsg.class);
            System.out.println("Received status message: " + msg);
            ws.convertAndSend("/topic/ourStatus", msg);
        } catch (IOException e) {
            throw new RuntimeException("解析设备状态消息失败", e);
        }
    }
}