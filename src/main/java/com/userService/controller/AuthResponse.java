package com.userService.controller;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String AccessToken;
    private String RefreshToken;
    private String message;
}
