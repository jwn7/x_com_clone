package com.example.x_com_clone.dto;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.Retweet;
import com.example.x_com_clone.domain.User; // 📌 추가: actionUser 필드용
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Post와 Retweet을 통합하여 타임라인에 표시할 항목의 DTO
 */
@Getter
@Builder
public class TimelineItemDto {
    private Long itemId; // 📌 추가: 템플릿의 item.itemId 오류 해결 (원본 Post의 ID 사용)
    private User actionUser; // 📌 추가: 이 항목을 생성한 사용자 (Post의 작성자 또는 Retweet을 수행한 사용자)
    private Post originalPost; // 📌 수정: 기존 post 필드를 originalPost로 변경하여 템플릿 일치
    private boolean isRetweet; // 리트윗인지 여부 (프로필 페이지 UI 분기용)
    private LocalDateTime createdAt; // 타임라인 정렬 기준 시간 (Post의 작성 시간 또는 Retweet의 생성 시간)
    private long retweetCount; // 해당 게시물의 전체 리트윗 수

    // Post 객체로부터 TimelineItemDto를 생성
    public static TimelineItemDto fromPost(Post post, long retweetCount) {
        return TimelineItemDto.builder()
                .itemId(post.getPostId()) // Post의 ID를 itemId로 설정
                .actionUser(post.getUser()) // 원본 게시물의 작성자가 actionUser
                .originalPost(post)
                .isRetweet(false)
                .createdAt(post.getCreatedAt())
                .retweetCount(retweetCount)
                .build();
    }

    // Retweet 객체로부터 TimelineItemDto를 생성
    public static TimelineItemDto fromRetweet(Retweet retweet, long retweetCount) {
        // Retweet은 항상 원본 Post를 포함해야 합니다.
        if (retweet.getPost() == null) {
            // 원본 게시물이 삭제된 경우 처리
            return null;
        }
        return TimelineItemDto.builder()
                .itemId(retweet.getPost().getPostId()) // 원본 Post의 ID를 itemId로 설정
                .actionUser(retweet.getUser()) // 리트윗을 수행한 사용자가 actionUser
                .originalPost(retweet.getPost()) // 원본 게시물을 포함
                .isRetweet(true)
                .createdAt(retweet.getCreatedAt()) // 리트윗 시점을 기준으로 정렬
                .retweetCount(retweetCount)
                .build();
    }
}