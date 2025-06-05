package com.userService.controller;

import com.userService.dto.Auth.*;
import com.userService.exception.UserAlreadyExistsException;
import com.userService.exception.UserCodeExistsException;
import com.userService.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.BadCredentialsException;
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
       return handleRequestProcess(() -> authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        return handleRequestProcess(() -> authService.refresh(refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return handleRequestProcess(() -> authService.register(request));
    }

    @PostMapping("/register/code")
    public ResponseEntity<AuthResponse> registerCode(@RequestBody RegisterCodeRequest request) {
        return handleRequestProcess(() -> authService.registerCode(request));
    }

    @PostMapping("/recover/password")
    public ResponseEntity<AuthResponse> recoverPasswordCode(@RequestBody RecoverRequest request) {
        return handleRequestProcess(() -> authService.recoverPasswordCode(request));
    }

    @PatchMapping("/password")
    public ResponseEntity<AuthResponse> recoverPassword(@RequestBody PasswordRequest request) {
        return handleRequestProcess(() -> authService.recoverPassword(request));
    }

    private ResponseEntity<AuthResponse> handleRequestProcess(ThrowingSupplier<AuthResponse> supplier) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(AuthResponse.builder().message("Error to send email").build());
        } catch (BadCredentialsException e){
            return ResponseEntity.status(401).body(AuthResponse.builder().message("Bad credentials").build());
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(AuthResponse.builder().message("User already exists").build());
        } catch (UserCodeExistsException e) {
            return ResponseEntity.status(409).body(AuthResponse.builder().message("Error: " + e).build());
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder().message("Internal Server Error " + e).build());
        }
    }
}
