package com.example.springboot_ch_1.util;

import org.springframework.data.domain.PageRequest;

import java.util.UUID;

/**
 * 栾俊豪 2020012422
 *
 * @author 栾俊豪
 * @Date 2023/11/10 23:48
 */

public class ShortIdGenerator {
    public static String generateShortId() {
        long timestamp = System.currentTimeMillis();
        UUID randomUUID = UUID.randomUUID();
        String randomComponent = randomUUID.toString().substring(0, 6);
        String shortId = timestamp + randomComponent;
        return shortId;
    }
    public static String generateTimeId() {
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        // 生成随机的 6 位字符
        String randomComponent = generateRandomString(6);
        // 拼接时间戳和随机字符
        return timestamp + randomComponent;
    }

    // 辅助方法：生成指定长度的随机字符
    private static String generateRandomString(int length) {
        // 定义字符集（可以根据需要修改）
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        // 随机生成指定长度的字符串
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
    public static long getPage(long totalPages, PageRequest pageRequest){
        int pageSize = pageRequest.getPageSize();
        if (totalPages % pageSize > 0) {
            totalPages = (int) Math.floor((double) totalPages / pageSize) + 1;
        } else {
            totalPages = (int) Math.floor((double) totalPages / pageSize);
        }
        return totalPages;
    }
}
