package com.userService.dto.Auth;

import lombok.Data;

@Data
public class RecoverRequest {
    String email;
    String locale;
}
