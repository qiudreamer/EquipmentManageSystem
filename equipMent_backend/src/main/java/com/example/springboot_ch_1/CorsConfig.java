package com.example.springboot_ch_1;

import com.example.springboot_ch_1.util.AllHref;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许来自 http://localhost:7778和socket的请求
        config.addAllowedOrigin(AllHref.front_url1);
        config.addAllowedOrigin(AllHref.front_url2);

        config.setAllowCredentials(true); // 允许携带凭据（Cookie）
        config.addAllowedMethod("*"); // 允许所有HTTP方法
        config.addAllowedHeader("*"); // 允许所有HTTP头部
        config.addExposedHeader("Content-Type"); // 暴露Content-Type头部

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}