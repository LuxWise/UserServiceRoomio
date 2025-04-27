package com.userService.service.Auth;

import com.userService.model.Users.Users;
import com.userService.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;


    public void increaseFailedAttempts(Users user) {
        int newFailedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailedAttempts);

        if (newFailedAttempts >= 5) {
            lockUserAccount(user);
        }

        userRepository.save(user);
    }

    public void resetFailedAttempts(String email) {
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
    }

    private void lockUserAccount(Users user) {
        user.setAccountLocked(true);
        userRepository.save(user);
    }

    public void unlockUserAccount(String email) {
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
    }

    public void updatePassword(Users user, String newPassword) {
        user.setPassword(newPassword);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
}

