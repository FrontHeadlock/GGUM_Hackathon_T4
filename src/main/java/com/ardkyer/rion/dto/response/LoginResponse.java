package com.ardkyer.rion.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String token;
    private UserResponse user;
}