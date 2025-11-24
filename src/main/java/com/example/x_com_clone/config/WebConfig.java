package com.example.x_com_clone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /uploads/** 로 요청이 들어오면
        // 실제 서버의 "uploads/" 폴더에서 파일을 찾아서 전달
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
