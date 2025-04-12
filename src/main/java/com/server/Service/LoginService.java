package com.server.Service;

import com.kurs.dto.LoginResponse;
import com.server.Entities.User;
import com.server.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository userRepository;

    @Autowired
    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse validateUser(String login, String password, String role) {
        boolean success = userRepository.findByLoginAndPasswordAndRole(login, password, role).isPresent();
        if (success) {
            return new LoginResponse(true, "OK");
        }
        return new LoginResponse(false, "ERROR");
    }

    public User getUser(String login, String password, String role) {
        return userRepository.findByLoginAndPasswordAndRole(login, password, role)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
