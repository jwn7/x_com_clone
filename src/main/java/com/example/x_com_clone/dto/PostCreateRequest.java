package com.example.x_com_clone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 게시물 생성 요청(Request)을 위한 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    // 게시물 내용 필드. 클라이언트가 JSON 형태로 이 값을 전달합니다.
    private String content;

    // 만약 나중에 String title; 필드를 추가해도 컨트롤러 시그니처는 유지됩니다.
}