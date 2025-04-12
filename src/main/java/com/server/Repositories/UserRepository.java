package com.server.Repositories;

import com.server.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginAndPasswordAndRole(String login, String password, String  role);

    Boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);
}
