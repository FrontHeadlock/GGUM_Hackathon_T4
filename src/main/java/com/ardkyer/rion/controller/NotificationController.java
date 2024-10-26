package com.ardkyer.rion.controller;

import com.ardkyer.rion.entity.Notification;
import com.ardkyer.rion.entity.User;
import com.ardkyer.rion.repository.NotificationRepository;
import com.ardkyer.rion.security.PrincipalDetails;
import com.ardkyer.rion.service.NotificationService;
import com.ardkyer.rion.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    // DTO 클래스 추가
    @Getter
    @Setter
    public static class NotificationResponse {
        private Long id;
        private String message;
        private String type;
        private boolean isRead;
        private LocalDateTime createdAt;
        private Long videoId;  // 관련 비디오 ID
        private String username;  // 알림을 받는 사용자 이름

        public NotificationResponse(Notification notification) {
            this.id = notification.getId();
            this.message = notification.getMessage();
            this.type = notification.getType();
            this.isRead = notification.isRead();
            this.createdAt = notification.getCreatedAt();
            this.videoId = notification.getVideo() != null ? notification.getVideo().getId() : null;
            this.username = notification.getUser().getUsername();
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications() {
        // 테스트용 고정 사용자
        User user = userService.findByUsername("as12");

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationResponses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read/{id}")
    public ResponseEntity<Map<String, Object>> readNotification(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationService.getNotification(id);
            notificationService.markAsRead(id);

            response.put("success", true);
            response.put("redirectUrl", notification.getVideo() != null
                    ? "/videos/detail/" + notification.getVideo().getId()
                    : "/notifications");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error reading notification: ", e);
            response.put("success", false);
            response.put("message", "Error reading notification: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete-read")
    public ResponseEntity<Map<String, Object>> deleteReadNotifications() {
        // 테스트용 고정 사용자
        User user = userService.findByUsername("as12");

        Map<String, Object> response = new HashMap<>();
        try {
            List<Notification> readNotifications = notificationRepository.findByUserAndIsReadTrue(user);
            notificationRepository.deleteAll(readNotifications);
            response.put("success", true);
            response.put("message", "읽은 알림이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting read notifications: ", e);
            response.put("success", false);
            response.put("message", "Error deleting read notifications: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}