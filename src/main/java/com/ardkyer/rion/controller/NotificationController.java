package com.ardkyer.rion.controller;

import com.ardkyer.rion.entity.Notification;
import com.ardkyer.rion.entity.NotificationType;
import com.ardkyer.rion.repository.NotificationRepository;
import com.ardkyer.rion.security.PrincipalDetails;
import com.ardkyer.rion.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String getNotifications(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(principalDetails.getUser());
        model.addAttribute("notifications", notifications);
        return "notifications";
    }

    @GetMapping("/read/{id}")
    public String readNotification(@PathVariable Long id) {
        try {
            Notification notification = notificationService.getNotification(id);
            notificationService.markAsRead(id);  // 알림 읽음 처리

            if (notification.getVideo() != null) {
                return "redirect:/videos/detail/" + notification.getVideo().getId();
            }
            return "redirect:/notifications";
        } catch (Exception e) {
            logger.error("Error reading notification: ", e);
            return "redirect:/notifications";
        }
    }

    @PostMapping("/delete-read")
    public String deleteReadNotifications(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            List<Notification> readNotifications = notificationRepository.findByUserAndIsReadTrue(principalDetails.getUser());
            notificationRepository.deleteAll(readNotifications);
            return "redirect:/notifications";
        } catch (Exception e) {
            logger.error("Error deleting read notifications: ", e);
            return "redirect:/notifications";
        }
    }
}
