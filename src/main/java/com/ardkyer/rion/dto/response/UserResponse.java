package com.ardkyer.rion.dto.response;

import com.ardkyer.rion.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String profilePicture;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private boolean emailVerified;
    private Set<String> roles;
    private boolean followedByCurrentUser;
    private int followingCount;
    private int followersCount;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .profilePicture(user.getProfilePicture())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .emailVerified(user.isEmailVerified())
                .roles(user.getRoles())
                .followedByCurrentUser(user.isFollowedByCurrentUser())
                .followingCount(user.getFollowing().size())
                .followersCount(user.getFollowers().size())
                .build();
    }
}