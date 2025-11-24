package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.dto.UserProfileUpdateRequest;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 📌 추가: 'log' 필드를 자동 생성합니다.
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j // ✅ 이 부분을 추가하여 'log' 심볼 오류를 해결합니다.
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 회원가입 메서드 (Signup)
    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent() ||
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 사용자 이름 또는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(request.getUsername(), request.getEmail(), encodedPassword);
        userRepository.save(newUser);
    }

    // 2. 로그인 인증 메서드 (Authenticate)
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 이름이 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    // 3. 프로필 조회 메서드 (Find Profile)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + username));
    }

    // 4. 프로필 업데이트 메서드 (Update Profile) - 파일 처리 로직 포함
    @Transactional
    public User updateProfile(Long currentUserId, UserProfileUpdateRequest request, MultipartFile profileImageFile) throws IOException {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 이름 중복 체크
        if (!user.getUsername().equals(request.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalStateException("이미 사용 중인 사용자 이름입니다.");
            }
        }

        String newProfileImageUrl = user.getProfileImageUrl();

        // 🚨 파일 처리 로직
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            log.info("새 프로필 이미지 파일 수신: {}", profileImageFile.getOriginalFilename()); // ✅ 'log' 사용 가능

            // 💡 파일 저장 (S3, GCS 등 클라우드 스토리지 또는 로컬 경로)
            // String uploadedUrl = fileStorageService.uploadFile(profileImageFile);

            // 더미 URL: 실제 구현 시 이 부분을 유효한 URL로 교체해야 합니다.
            newProfileImageUrl = "/uploads/profile/" + user.getUserId() + "_" + profileImageFile.getOriginalFilename();
            // 💡 여기서 실제 파일을 저장하는 코드가 들어가야 합니다.

        } else if (request.getProfileImageUrl() != null && request.getProfileImageUrl().isEmpty()) {
            // 💡 기존 이미지 URL 필드를 비웠다면, 이미지를 제거하는 것으로 처리할 수 있습니다.
            newProfileImageUrl = null;
        }


        user.updateProfile(
                request.getUsername(),
                request.getBio(),
                newProfileImageUrl
        );
        return user;
    }
}