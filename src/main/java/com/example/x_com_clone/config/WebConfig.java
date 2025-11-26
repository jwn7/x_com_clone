package com.example.x_com_clone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 📌 UserService의 UPLOAD_DIR과 일치하는 절대 경로 (file:/// 접두사 필수)
    private static final String UPLOAD_RESOURCE_LOCATION = "file:///C:/xcom_upload_folder/uploads/profile/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /uploads/profile/** 요청이 들어오면
        registry.addResourceHandler("/uploads/profile/**")
                // 실제 로컬 파일 시스템의 경로에서 파일을 찾아서 전달
                .addResourceLocations(UPLOAD_RESOURCE_LOCATION);
    }
}