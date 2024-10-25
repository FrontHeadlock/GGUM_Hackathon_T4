package com.ardkyer.rion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String postTitle;  // 게시글 제목
    private String commenterName;  // 댓글 작성자 이름
    private String commentContent;  // 댓글 내용

    private boolean isRead;  // 알림 읽음 여부
    private String message;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    // video 엔티티에서 ID를 가져오는 메소드
    public Long getVideoId() {
        return video != null ? video.getId() : null;
    }

    // 기본 생성자
    public Notification() {
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
}