package com.ardkyer.rion.service;

import com.ardkyer.rion.entity.*;
import com.ardkyer.rion.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createReplyNotification(User recipient, Reply reply) {
        Comment parentComment = reply.getParentComment();
        Video video = parentComment.getVideo();

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(NotificationType.REPLY);
        notification.setVideo(video);
        notification.setComment(parentComment);
        notification.setReply(reply);
        notification.setPostTitle(video.getTitle());
        notification.setCommenterName(reply.getUser().getUsername());
        notification.setCommentContent(reply.getContent());
        notification.setMessage(String.format("[%s] %s님이 회원님의 댓글에 답글을 달았습니다: %s",
                video.getTitle(),
                reply.getUser().getUsername(),
                reply.getContent()));

        return notificationRepository.save(notification);
    }

    // 다른 종류의 알림을 위한 메소드들...
    public Notification createCommentNotification(User recipient, Comment comment) {
        Video video = comment.getVideo();

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(NotificationType.COMMENT);
        notification.setVideo(video);
        notification.setComment(comment);
        notification.setPostTitle(video.getTitle());
        notification.setCommenterName(comment.getUser().getUsername());
        notification.setCommentContent(comment.getContent());
        notification.setMessage(String.format("[%s] %s님이 댓글을 달았습니다: %s",
                video.getTitle(),
                comment.getUser().getUsername(),
                comment.getContent()));

        return notificationRepository.save(notification);
    }

    public Notification getNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
    }

    public void markAsRead(Long id) {
        Notification notification = getNotification(id);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteReadNotifications(User user) {
        List<Notification> readNotifications = notificationRepository.findByUserAndIsReadTrue(user);
        notificationRepository.deleteAll(readNotifications);
    }
}