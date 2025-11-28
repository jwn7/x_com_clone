package com.example.x_com_clone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 📌 모든 업로드 파일의 최상위 경로 (프로필 + 미디어 모두 포함)
    private static final String UPLOAD_ROOT = "file:///C:/xcom_upload_folder/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // [수정됨] /uploads/ 로 시작하는 모든 요청(**)을 처리
        // 예: /uploads/profile/a.jpg -> C:/xcom_upload_folder/uploads/profile/a.jpg
        // 예: /uploads/media/b.jpg   -> C:/xcom_upload_folder/uploads/media/b.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(UPLOAD_ROOT);
    }
}