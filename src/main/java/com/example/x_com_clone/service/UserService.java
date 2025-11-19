package com.example.x_com_clone.Service;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.UserSignupRequest; // 💡 DTO 임포트 추가
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 사용자를 등록합니다 (비밀번호를 암호화하여 저장).
     */
    @Transactional // 쓰기 작업이므로 @Transactional 필수
    // 💡 시그니처 변경: String 인자 대신 DTO 객체를 받도록 수정
    public User signup(UserSignupRequest request) {

        // DTO에서 데이터 추출
        String username = request.getUsername();
        String email = request.getEmail();
        String rawPassword = request.getPassword(); // 평문 비밀번호

        validateDuplicateUser(username, email);

        // 💡 암호화 과정: rawPassword를 해시하여 hashedPassword로 저장
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // User 엔티티 생성 (hashedPassword를 사용)
        User user = new User(username, email, hashedPassword);

        return userRepository.save(user);
    }

    private void validateDuplicateUser(String username, String email) {
        // 기존 로직 유지: 사용자 이름 및 이메일 중복 검사
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }
}