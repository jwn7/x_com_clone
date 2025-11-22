package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 기능
     * @param request 회원가입 요청 DTO (username, email, password)
     */
    @Transactional
    public void signup(UserSignupRequest request) {
        // 1. 중복 사용자 체크
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPasswordHash = passwordEncoder.encode(request.getPassword());

        // 3. User 도메인 객체 생성 (엔티티 생성자를 사용하도록 변경)
        User newUser = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPasswordHash // password -> passwordHash로 변경
        );

        // 4. 저장
        userRepository.save(newUser);
    }

    //---------------------------------------------------------

    /**
     * 로그인 인증 기능
     * @param username 사용자 이름
     * @param rawPassword 입력된 비밀번호 (암호화되지 않은 원문)
     * @return 인증된 User 객체
     * @throws IllegalArgumentException 인증 실패 시 발생
     */
    public User authenticate(String username, String rawPassword) {
        // 1. 사용자 이름으로 DB에서 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 이름 또는 비밀번호가 잘못되었습니다."));

        // 2. 비밀번호 일치 여부 확인
        // User 엔티티의 passwordHash 필드를 사용하도록 변경
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("사용자 이름 또는 비밀번호가 잘못되었습니다.");
        }

        // 3. 인증 성공
        return user;
    }

    //---------------------------------------------------------

    /**
     * 사용자 이름으로 사용자 정보를 조회 (프로필 페이지용)
     * @param username 조회할 사용자 이름
     * @return 조회된 User 객체
     * @throws IllegalArgumentException 해당 사용자가 없을 경우 발생
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + username));
    }
}