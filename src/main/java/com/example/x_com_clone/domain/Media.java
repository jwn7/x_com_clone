package com.example.x_com_clone.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    // Post 1개에 여러 Media가 매달리는 ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String fileUrl;  // 저장된 파일 위치 (/uploads/xxx.png)

    @Column(nullable = false)
    private String fileType; // image/png, image/jpeg 등
}
