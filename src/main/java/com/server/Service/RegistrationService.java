package com.server.Service;

import com.kurs.dto.RegistrationResponse;
import com.server.Entities.User;
import com.server.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserRepository userRepository;

    @Autowired
    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegistrationResponse register(String name, String login, String password) {
        if (userRepository.existsByLogin(login)) {
            return new RegistrationResponse(false, "Пользователь с таким логином уже существует.");
        }

        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setPassword(password);
        user.setRole("Клиент");

        userRepository.save(user);
        return new RegistrationResponse(true, "Регистрация прошла успешно.");
    }

    public User getUser(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
