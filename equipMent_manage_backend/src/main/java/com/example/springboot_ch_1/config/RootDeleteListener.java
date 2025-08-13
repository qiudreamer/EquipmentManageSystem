package com.example.springboot_ch_1.config;

import com.example.springboot_ch_1.entity.msg.StewardStatusMsg;
import org.springframework.data.redis.connection.MessageListener;
import com.example.springboot_ch_1.entity.msg.DeviceOutOrOnMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/27
 * Time: 15:34
 * Description:
 */
@Component
public class RootDeleteListener implements MessageListener {

    @Autowired
    private SimpMessagingTemplate ws;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            StewardStatusMsg msg = mapper.readValue(message.getBody(), StewardStatusMsg.class);
            System.out.println("Received status message: " + msg);
            ws.convertAndSend("/topic/stewardStatus", msg);
        } catch (IOException e) {
            throw new RuntimeException("解析权限状态消息失败", e);
        }
    }
}
