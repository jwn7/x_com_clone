package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Follow;
import com.example.x_com_clone.domain.FollowId;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    /** 팔로우 하기 */
    @Transactional
    public void follow(User follower, User following) {
        // 자기 자신 팔로우 방지
        if (follower.getUserId().equals(following.getUserId())) {
            throw new IllegalStateException("자기 자신은 팔로우할 수 없습니다.");
        }

        // 이미 팔로우 중이면 그냥 무시
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            return;
        }

        FollowId id = new FollowId(follower.getUserId(), following.getUserId());

        Follow follow = Follow.builder()
                .id(id)
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    /** 언팔로우 */
    @Transactional
    public void unfollow(User follower, User following) {
        // 있으면 삭제
        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    /** A가 B를 팔로우 중인지 확인 */
    @Transactional(readOnly = true)
    public boolean isFollowing(User follower, User following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    /** 팔로워 수 */
    @Transactional(readOnly = true)
    public long countFollowers(User user) {
        return followRepository.findByFollowing(user).size();
    }

    /** 팔로잉 수 */
    @Transactional(readOnly = true)
    public long countFollowing(User user) {
        return followRepository.findByFollower(user).size();
    }

    /** 팔로워 리스트 (필요하면 사용) */
    @Transactional(readOnly = true)
    public List<Follow> getFollowers(User user) {
        return followRepository.findByFollowing(user);
    }

    /** 팔로잉 리스트 (필요하면 사용) */
    @Transactional(readOnly = true)
    public List<Follow> getFollowing(User user) {
        return followRepository.findByFollower(user);
    }
}
