package com.ardkyer.rion.controller;

import com.ardkyer.rion.dto.*;
import com.ardkyer.rion.entity.*;
import com.ardkyer.rion.repository.NotificationRepository;
import com.ardkyer.rion.security.PrincipalDetails;
import com.ardkyer.rion.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ardkyer.rion.dto.request.LoginRequest;
import com.ardkyer.rion.dto.request.SignupRequest;
import com.ardkyer.rion.dto.response.LoginResponse;
import com.ardkyer.rion.dto.response.UserResponse;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "User management API")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    @PostMapping("/auth/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        try {
            UserResponse response = userService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "Get unread notifications count")
    public ResponseEntity<?> getUnreadNotificationsCount(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        int unreadCount = notificationRepository.countByUserAndIsReadFalse(
                principalDetails.getUser()
        );
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    // 기존의 다른 API 엔드포인트들...
}