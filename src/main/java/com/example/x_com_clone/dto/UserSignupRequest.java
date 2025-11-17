package com.example.x_com_clone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {

    private String username;
    private String email;
    private String password; // 평문 비밀번호를 받습니다.
}