package com.server.Service.AdminService;

import com.kurs.dto.AdminDTOs.UserDTO;
import com.server.Entities.User;
import com.server.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;

    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
           UserDTO userDTO = new UserDTO();
           userDTO.setId(user.getId());
           userDTO.setName(user.getName());
           userDTO.setFirstName(user.getLastName());
           userDTO.setSurname(user.getSurname());
           userDTO.setEmail(user.getEmail());
           userDTO.setBirthDate(user.getBirthDate());
           userDTO.setPhone(user.getPhoneNumber());
           userDTO.setRole(user.getRole());
           return userDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteUser(int userId) {
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean promoteToAdmin(int userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return false;
            }

            user.setRole("Админ");
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
