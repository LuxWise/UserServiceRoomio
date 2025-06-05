package com.userService.dto.Auth;

import lombok.Data;

@Data
public class RegisterCodeRequest {
    String email;
    String locale;
}
