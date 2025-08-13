package com.example.springboot_ch_1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 17:34
 * Description:
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer container(
            RedisConnectionFactory factory,
            DeviceStatusListener statusListener,
            DeviceDeleteListener deleteListener,
            DeviceChangeListener deviceChangeListener,
            RootDeleteListener rootDeleteListener,
            UserStatusListener userStatusListener,
            DeviceBorrowStatusListener deviceBorrowStatusListener
            ) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        // 用默认适配器即可，因为它们都实现了 MessageListener
        container.addMessageListener(new MessageListenerAdapter(statusListener),
                new PatternTopic("device.status.manage"));
        container.addMessageListener(new MessageListenerAdapter(deleteListener),
                new PatternTopic("device.delete.manage"));
        container.addMessageListener(new MessageListenerAdapter(deviceChangeListener),
                new PatternTopic("device.change.manage"));
        container.addMessageListener(new MessageListenerAdapter(rootDeleteListener),
                new PatternTopic("user.steward.manage"));
        container.addMessageListener(new MessageListenerAdapter(userStatusListener),
                new PatternTopic("user.account.manage"));
        container.addMessageListener(new MessageListenerAdapter(deviceBorrowStatusListener),
                new PatternTopic("device.borrow.manage"));
        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}