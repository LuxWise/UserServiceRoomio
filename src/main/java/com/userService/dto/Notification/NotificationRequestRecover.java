package com.userService.dto.Notification;

import lombok.Data;

@Data
public class NotificationRequestRecover {
    private String to;
    private String subject;
    private String code;
}
