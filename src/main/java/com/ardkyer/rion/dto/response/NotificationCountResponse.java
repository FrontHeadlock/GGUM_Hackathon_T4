package com.ardkyer.rion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationCountResponse {
    private int unreadCount;
}