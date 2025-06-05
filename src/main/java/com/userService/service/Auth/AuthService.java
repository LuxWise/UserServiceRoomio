package com.userService.service.Auth;

import com.userService.controller.AuthResponse;
import com.userService.dto.Auth.*;
import com.userService.exception.UserAlreadyExistsException;
import com.userService.exception.UserCodeExistsException;
import com.userService.model.Roles;
import com.userService.model.UserCodes;
import com.userService.model.Users;
import com.userService.repository.RolesRepository;
import com.userService.repository.UserCodeRepository;
import com.userService.repository.UsersRepository;
import com.userService.service.Jwt.JwtService;
import com.userService.service.Notification.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository userRepository;
    private final RolesRepository rolesRepository;
    private final UserCodeRepository userCodeRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationPublisher notificationPublisher;

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }


    public AuthResponse login(LoginRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        if (user.getAccountLocked()) {
            throw new LockedException("locked account.Change password.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            userService.increaseFailedAttempts(user);
            throw new BadCredentialsException("Bad credentials");
        }

        userService.resetFailedAttempts(user.getEmail());
        String accessToken = jwtService.getAccessToken(user);
        String refreshToken = jwtService.getRefreshToken(user);
        return AuthResponse.builder()
            .AccessToken(accessToken)
            .RefreshToken(refreshToken)
            .message("Access Allowed")
            .build();
    }

    public AuthResponse refresh(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            return AuthResponse.builder().message("Invalid or missing token").build();
        }

        String token = refreshToken.substring(7);
        String userId = jwtService.extractUserIdFromRefreshToken(token);

        Users user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        String newAccessToken = jwtService.getAccessToken(user);
        String newRefreshToken = jwtService.getRefreshToken(user);

        return AuthResponse.builder()
            .AccessToken(newAccessToken)
            .RefreshToken(newRefreshToken)
            .message("Token refreshed")
            .build();
    }

    public AuthResponse registerCode(RegisterCodeRequest request) {
        Optional<Users> user = userRepository.findByEmail(request.getEmail());
        Optional<UserCodes> existingCode = userCodeRepository.findByEmail(request.getEmail());

        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String code = generateCode();

        if (existingCode.isPresent()) {
            UserCodes codeEntity = existingCode.get();
            codeEntity.setCode(code);
            codeEntity.setCreatedAt(LocalDateTime.now());
            userCodeRepository.save(codeEntity);
        } else {
            UserCodes newCode = new UserCodes();
            newCode.setEmail(request.getEmail());
            newCode.setCode(code);
            newCode.setCreatedAt(LocalDateTime.now());
            userCodeRepository.save(newCode);
        }

        notificationPublisher.publishNotificationRegisterCode(request.getEmail(), "Código de verificación", code);

        return AuthResponse.builder().message("Verification code sent").build();
    }


    public AuthResponse register(RegisterRequest request) {
        System.out.println(request.getEmail() + " " + request.getCode() + " " + request.getPassword() );
        Roles userRole = rolesRepository.findById(request.getRoleId()).orElse(null);
        Optional<Users> user = userRepository.findByEmail(request.getEmail());
        Optional<UserCodes> existingCode = userCodeRepository.findByEmail(request.getEmail());

        System.out.println(user);
        System.out.println(existingCode);

        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        if (existingCode.isEmpty() || existingCode.get().getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(60)) ) {
            throw new UserCodeExistsException("User code unavailable");
        }

        if (!existingCode.get().getCode().equals(request.getCode())) {
            throw new UserCodeExistsException("Invalid verification code");
        }

        Users user_data =  new Users();
        user_data.setName(request.getName());
        user_data.setLastname(request.getLastname());
        user_data.setEmail(request.getEmail());
        user_data.setPhone(request.getPhone());
        user_data.setPassword(passwordEncoder.encode(request.getPassword()));
        user_data.setRole(userRole);

        UserCodes codeRecord = existingCode.get();
        codeRecord.setCode("");


        userRepository.save(user_data);
        userCodeRepository.save(codeRecord);
        notificationPublisher.publishNotificationRegister(request.getEmail(),"Welcome to Roomio");

        return AuthResponse.builder().message("Account create").build();
    }

    public AuthResponse recoverPasswordCode(RecoverRequest request) {
        Optional<UserCodes> existingCode = userCodeRepository.findByEmail(request.getEmail());

        String code = generateCode();

        if (existingCode.isPresent()) {
            UserCodes codeEntity = existingCode.get();
            codeEntity.setCode(code);
            codeEntity.setCreatedAt(LocalDateTime.now());
            userCodeRepository.save(codeEntity);
        } else {
            UserCodes newCode = new UserCodes();
            newCode.setEmail(request.getEmail());
            newCode.setCode(code);
            newCode.setCreatedAt(LocalDateTime.now());
            userCodeRepository.save(newCode);
        }

        userRepository.findByEmail(request.getEmail())
            .ifPresent(user -> notificationPublisher
                .publishNotificationRecover(user.getEmail(), "Codigo de verificación", code));

        return AuthResponse.builder().message("Code send").build();
    }

    public AuthResponse recoverPassword(PasswordRequest request) {
        System.out.println(request.getEmail() + " " + request.getCode() + " " + request.getNewPassword() );
        Optional<Users> user = userRepository.findByEmail(request.getEmail());
        Optional<UserCodes> existingCode = userCodeRepository.findByEmail(request.getEmail());

        if (existingCode.isEmpty() || existingCode.get().getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(60)) ) {
            throw new UserCodeExistsException("User code unavailable");
        }

        if (!existingCode.get().getCode().equals(request.getCode())) {
            throw new UserCodeExistsException("Invalid verification code");
        }

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        UserCodes codeRecord = existingCode.get();
        codeRecord.setCode("");
        userCodeRepository.save(codeRecord);

        Users userRecord = user.get();
        userRecord.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userRecord);

        return AuthResponse.builder().message("Password reset").build();
    }
}