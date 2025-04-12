package com.server.Service;

import com.kurs.dto.UserProfile;
import com.server.Entities.User;
import com.server.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final UserRepository userRepository;

    @Autowired
    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfile getUserProfile(User user) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(user.getId());
        userProfile.setName(user.getName());
        userProfile.setLastName(user.getLastName());
        userProfile.setSurname(user.getSurname());
        userProfile.setEmail(user.getEmail());
        userProfile.setPhone(user.getPhoneNumber());
        userProfile.setBirthDate(user.getBirthDate());
        return userProfile;
    }

    public boolean updateUserProfile(User user, UserProfile updatedProfile) {
        try {
            user.setName(updatedProfile.getName());
            user.setLastName(updatedProfile.getLastName());
            user.setSurname(updatedProfile.getSurname());
            user.setEmail(updatedProfile.getEmail());
            user.setPhoneNumber(updatedProfile.getPhone());
            user.setBirthDate(updatedProfile.getBirthDate());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
