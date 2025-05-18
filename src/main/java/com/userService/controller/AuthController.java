package com.userService.controller;


import com.userService.dto.Auth.LoginRequest;
import com.userService.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/health")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("User Service is running");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

       return handleRequestProcess(() -> {
            return  authService.login(request);
        });
    }

    private ResponseEntity<AuthResponse> handleRequestProcess(ThrowingSupplier<AuthResponse> supplier) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(AuthResponse.builder().message("Error to send email").build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder().message("Internal Server Error").build());
        }
    }
}
