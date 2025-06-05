package com.userService.dto.Auth;

import lombok.Data;

@Data
public class PasswordRequest {
    String email;
    String newPassword;
    String code;

}
