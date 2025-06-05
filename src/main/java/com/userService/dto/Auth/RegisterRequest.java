package com.userService.dto.Auth;

import lombok.Data;

@Data
public class RegisterRequest {
    String name;
    String lastname;
    String email;
    String phone;
    String password;
    Integer roleId;
    String code;

}
