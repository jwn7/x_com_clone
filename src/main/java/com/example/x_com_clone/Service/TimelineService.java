package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.Retweet;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.TimelineItemDto;
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.RetweetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final PostRepository postRepository;
    private final RetweetRepository retweetRepository;

    /**
     * 모든 게시물과 리트윗을 통합하여 타임라인 항목 목록을 생성합니다.
     * 최종적으로 생성 시간 내림차순으로 정렬됩니다.
     */
    @Transactional
    public List<TimelineItemDto> getGlobalTimeline() {

        // 1. 모든 Post(원본 게시물)를 가져옵니다.
        List<Post> posts = postRepository.findAll();

        // 2. 모든 Retweet(리트윗)을 가져옵니다.
        List<Retweet> retweets = retweetRepository.findAll();

        // 3. 각 Post에 대한 리트윗 수를 미리 계산합니다.
        Map<Long, Long> retweetCounts = retweets.stream()
                .map(Retweet::getPost)
                .filter(post -> post != null)
                .collect(Collectors.groupingBy(Post::getPostId, Collectors.counting()));

        // 4. Post를 TimelineItemDto로 변환
        Stream<TimelineItemDto> postItems = posts.stream()
                .map(post -> {
                    long count = retweetCounts.getOrDefault(post.getPostId(), 0L);
                    return TimelineItemDto.fromPost(post, count);
                });

        // 5. Retweet을 TimelineItemDto로 변환
        Stream<TimelineItemDto> retweetItems = retweets.stream()
                .filter(retweet -> retweet.getPost() != null)
                .map(retweet -> {
                    long count = retweetCounts.getOrDefault(retweet.getPost().getPostId(), 0L);
                    return TimelineItemDto.fromRetweet(retweet, count);
                })
                .filter(item -> item != null); // fromRetweet에서 null 반환 가능성 대비 필터링

        // 6. 두 스트림을 통합하고, 생성 시간 내림차순으로 정렬합니다.
        return Stream.concat(postItems, retweetItems)
                .sorted(Comparator.comparing(TimelineItemDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    // =========================================================================
    // 특정 사용자의 타임라인 조회 (요청하신 메서드)
    // =========================================================================

    /**
     * 특정 사용자가 작성한 모든 게시물과 해당 사용자가 리트윗한 항목을 통합하여
     * 타임라인 항목 목록을 생성합니다. 최종적으로 생성 시간 내림차순으로 정렬됩니다.
     * @param user 타임라인을 조회할 사용자 객체
     * @return 타임라인 항목 DTO 목록
     */
    @Transactional
    public List<TimelineItemDto> getTimelineForUser(User user) {

        // 1. 해당 사용자가 작성한 모든 Post(원본 게시물)를 가져옵니다.
        // PostRepository에 findByUser(User user)가 존재한다고 가정합니다.
        List<Post> userPosts = postRepository.findByUser(user);

        // 2. 해당 사용자가 수행한 모든 Retweet(리트윗)을 가져옵니다.
        // RetweetRepository에 findByUser(User user)가 존재한다고 가정합니다.
        List<Retweet> userRetweets = retweetRepository.findByUser(user);

        // 3. 전체 Retweet을 가져와 리트윗 수를 미리 계산합니다. (타임라인 항목에 리트윗 횟수 표시용)
        List<Retweet> allRetweets = retweetRepository.findAll();
        Map<Long, Long> retweetCounts = allRetweets.stream()
                .map(Retweet::getPost)
                .filter(post -> post != null)
                .collect(Collectors.groupingBy(Post::getPostId, Collectors.counting()));

        // 4. userPosts(원본 게시물)를 TimelineItemDto로 변환
        Stream<TimelineItemDto> postItems = userPosts.stream()
                .map(post -> {
                    long count = retweetCounts.getOrDefault(post.getPostId(), 0L);
                    return TimelineItemDto.fromPost(post, count);
                });

        // 5. userRetweets(리트윗)을 TimelineItemDto로 변환
        Stream<TimelineItemDto> retweetItems = userRetweets.stream()
                .filter(retweet -> retweet.getPost() != null)
                .map(retweet -> {
                    long count = retweetCounts.getOrDefault(retweet.getPost().getPostId(), 0L);
                    return TimelineItemDto.fromRetweet(retweet, count);
                })
                .filter(item -> item != null);

        // 6. 두 스트림을 통합하고, 생성 시간 내림차순으로 정렬합니다.
        return Stream.concat(postItems, retweetItems)
                .sorted(Comparator.comparing(TimelineItemDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}