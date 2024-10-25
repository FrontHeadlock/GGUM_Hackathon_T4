package com.ardkyer.rion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public void setParentComment(Comment comment) {
        this.parentComment = comment;
        if (comment.getVideo() != null) {
            this.video = comment.getVideo();  // 자동으로 비디오 정보 설정
        }
    }

    @Column(nullable = false)
    private String content;

    private LocalDateTime createdAt;
}