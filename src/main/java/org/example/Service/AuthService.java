package org.example.Service;

import org.example.Domain.User;
import org.example.Repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(String fullName, String email,  String password) {
        if (password.length() < 6) return false;
        if (userRepository.findByEmail(email).isPresent()) return false;

        User user = new User(fullName, email,  password);
        userRepository.save(user);
        return true;
    }

    public boolean login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            currentUser = userOpt.get();
            return true;
        }
        return false;
    }

    public boolean updateProfile(String username , String  email){
           userRepository.updateProfile(username , email);
           return true;
    }

    public boolean changePassword(String password , UUID userId){
        userRepository.updatePassword(password , userId);
        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
