package com.ardkyer.rion.controller;

import com.ardkyer.rion.entity.ItemStatus;
import com.ardkyer.rion.entity.Video;
import com.ardkyer.rion.service.VideoService;
import com.ardkyer.rion.service.ItemUnitService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/items/units")
public class ItemUnitController {

    @Autowired
    private ItemUnitService itemUnitService;

    @Autowired
    private VideoService videoService;

    @Getter
    @Setter
    public static class StatusUpdateRequest {
        private String status;
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUnitStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {  // Authentication 파라미터만 제거
        try {
            ItemStatus newStatus = ItemStatus.valueOf(request.getStatus());

            // 테스트용 더미 Authentication 생성
            Authentication dummyAuth = new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return null;
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) {
                }

                @Override
                public String getName() {
                    return "as12";  // 여기서 고정된 사용자명 사용
                }
            };

            // 기존 서비스 메소드 그대로 사용
            itemUnitService.updateUnitStatus(id, newStatus, dummyAuth);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "상태가 변경되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "잘못된 상태값입니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}