package com.ardkyer.rion.controller;

import com.ardkyer.rion.entity.Notification;
import com.ardkyer.rion.entity.User;
import com.ardkyer.rion.repository.NotificationRepository;
import com.ardkyer.rion.security.PrincipalDetails;
import com.ardkyer.rion.service.NotificationService;
import com.ardkyer.rion.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;  // UserService 추가

    // 모든 알림을 조회하는 메서드
    @GetMapping
    public Map<String, Object> getNotifications() {  // Authentication 제거
        // 테스트용 고정 사용자
        User user = userService.findByUsername("as12");
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifications);
        return response;
    }

    // 특정 알림을 읽음 상태로 표시하는 메서드
    @PutMapping("/read/{id}")
    public Map<String, Object> readNotification(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationService.getNotification(id);
            notificationService.markAsRead(id);

            response.put("success", true);
            response.put("redirectUrl", notification.getVideo() != null
                    ? "/videos/detail/" + notification.getVideo().getId()
                    : "/notifications");
        } catch (Exception e) {
            logger.error("Error reading notification: ", e);
            response.put("success", false);
            response.put("message", "Error reading notification: " + e.getMessage());
        }
        return response;
    }

    // 읽은 알림을 삭제하는 메서드
    @DeleteMapping("/delete-read")
    public Map<String, Object> deleteReadNotifications() {  // Authentication 제거
        Map<String, Object> response = new HashMap<>();
        try {
            // 테스트용 고정 사용자
            User user = userService.findByUsername("as12");
            List<Notification> readNotifications = notificationRepository.findByUserAndIsReadTrue(user);
            notificationRepository.deleteAll(readNotifications);
            response.put("success", true);
            response.put("message", "읽은 알림이 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("Error deleting read notifications: ", e);
            response.put("success", false);
            response.put("message", "Error deleting read notifications: " + e.getMessage());
        }
        return response;
    }
}
