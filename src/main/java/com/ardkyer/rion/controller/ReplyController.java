package com.ardkyer.rion.controller;

import com.ardkyer.rion.entity.Reply;
import com.ardkyer.rion.entity.User;
import com.ardkyer.rion.service.ReplyService;
import com.ardkyer.rion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private UserService userService;

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> addReply(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.findByUsername(authentication.getName());
            Reply reply = replyService.addReply(commentId, request.get("content"), user);

            // 응답 데이터 직접 구성
            Map<String, Object> replyData = new HashMap<>();
            replyData.put("id", reply.getId());
            replyData.put("content", reply.getContent());
            replyData.put("createdAt", reply.getCreatedAt());

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", reply.getUser().getId());
            userData.put("username", reply.getUser().getUsername());
            replyData.put("user", userData);

            response.put("success", true);
            response.put("reply", replyData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}