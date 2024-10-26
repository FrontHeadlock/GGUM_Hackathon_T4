//package com.ardkyer.rion.controller;
//
//import com.ardkyer.rion.dto.request.LoginRequest;
//import com.ardkyer.rion.dto.request.SignupRequest;
//import com.ardkyer.rion.dto.response.LoginResponse;
//import com.ardkyer.rion.dto.response.NotificationCountResponse;
//import com.ardkyer.rion.dto.response.UserResponse;
//import com.ardkyer.rion.entity.User;
//import com.ardkyer.rion.repository.NotificationRepository;
//import com.ardkyer.rion.security.PrincipalDetails;
//import com.ardkyer.rion.service.UserService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//@RestController
//@RequestMapping("/api")
//@Tag(name = "Web", description = "REST API endpoints")
//@RequiredArgsConstructor
//public class WebController {
//
//    private final UserService userService;
//    private final NotificationRepository notificationRepository;
//
//    @PostMapping("/auth/login")
//    @Operation(summary = "Login user")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        try {
//            LoginResponse response = userService.login(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PostMapping("/auth/register")
//    @Operation(summary = "Register new user")
//    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
//        try {
//            UserResponse response = userService.registerUser(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/notifications/unread-count")
//    @Operation(summary = "Get unread notifications count")
//    public ResponseEntity<?> getUnreadNotificationsCount(
//            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        int unreadCount = notificationRepository.countByUserAndIsReadFalse(
//                principalDetails.getUser()
//        );
//        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
//    }
//}