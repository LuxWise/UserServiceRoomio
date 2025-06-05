package com.userService.dto.Notification;

import lombok.Data;

@Data
public class NotificationRequestRegisterCode {
    private String to;
    private String subject;
    private String code;
}
