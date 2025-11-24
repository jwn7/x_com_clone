package com.example.x_com_clone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {

    // 💡 사용자 이름은 고유해야 하므로 변경 시 중복 검사가 필요할 수 있습니다.
    // 여기서는 간단하게 기존 이름과 다른 경우에만 중복 검사를 한다고 가정합니다.
    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(min = 3, max = 50, message = "사용자 이름은 3자 이상 50자 이하여야 합니다.")
    private String username;

    @Size(max = 500, message = "소개는 500자 이하여야 합니다.")
    private String bio;

    // 프로필 이미지 URL은 직접 업로드하거나 URL을 입력받을 수 있습니다.
    private String profileImageUrl;
}