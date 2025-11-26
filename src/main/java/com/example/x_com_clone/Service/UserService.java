package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.dto.UserProfileUpdateRequest;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 📌 로컬 파일 저장 절대 경로 설정 (🚨 폴더를 수동으로 생성해야 합니다!)
    private static final String UPLOAD_DIR = "C:/xcom_upload_folder/uploads/profile/";

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

    // 4. 프로필 업데이트 메서드 (Update Profile)
    @Transactional
    public User updateProfile(Long currentUserId, UserProfileUpdateRequest request, MultipartFile profileImageFile) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 이름 중복 체크
        if (!user.getUsername().equals(request.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalStateException("이미 사용 중인 사용자 이름입니다.");
            }
        }

        String newProfileImageUrl = user.getProfileImageUrl();

        // 🚨 파일 처리 로직 시작
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            log.info("새 프로필 이미지 파일 수신: {}", profileImageFile.getOriginalFilename());

            try {
                // 1. 저장 디렉토리 생성
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // 2. 파일 이름 설정 (UUID 사용)
                String originalFilename = profileImageFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // 3. 파일 저장
                Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.copy(profileImageFile.getInputStream(), filePath);

                // 4. 웹 접근 URL 생성 (DB 저장용)
                // WebConfig에서 /uploads/profile/** 로 매핑됩니다.
                newProfileImageUrl = "/uploads/profile/" + uniqueFileName;
                log.info("새 프로필 이미지 URL 생성: {}", newProfileImageUrl);

            } catch (IOException e) {
                log.error("프로필 이미지 저장 실패. 경로: {}", UPLOAD_DIR, e);
                // 파일 저장 실패 시 프로필 업데이트를 중단하고 예외를 던집니다.
                throw new RuntimeException("프로필 이미지 저장 중 오류가 발생했습니다.", e);
            }

        } else if (request.getProfileImageUrl() != null && request.getProfileImageUrl().isEmpty()) {
            // 💡 프로필 수정 폼에서 기존 이미지를 삭제한 경우 (clear 요청)
            newProfileImageUrl = null;
        }


        user.updateProfile(
                request.getUsername(),
                request.getBio(),
                newProfileImageUrl
        );
        userRepository.save(user); // 변경 사항 저장
        return user;
    }

    // 📌 User 클래스에 updateProfile 메서드가 있다고 가정
}