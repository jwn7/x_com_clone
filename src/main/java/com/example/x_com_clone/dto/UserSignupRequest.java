// com.example.x_com_clone.dto.UserSignupRequest.java

package com.example.x_com_clone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {

    // 💡 HTML 폼에 맞춰 displayName 필드를 추가합니다.

    private String username;
    private String email;
    private String password;
}