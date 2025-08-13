package com.example.springboot_ch_1;

import java.util.UUID;

public class ShortIdGenerator {
    public static String generateShortId() {
        long timestamp = System.currentTimeMillis();
        UUID randomUUID = UUID.randomUUID();
        String randomComponent = randomUUID.toString().substring(0, 6);
        String shortId = timestamp + randomComponent;
        return shortId;
    }
}
