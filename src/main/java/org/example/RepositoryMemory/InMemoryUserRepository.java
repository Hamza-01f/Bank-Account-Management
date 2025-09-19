package org.example.RepositoryMemory;

import org.example.Domain.User;
import org.example.Repository.UserRepository;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getEmail(), user);
    }
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    @Override
    public boolean updateProfile(String username , String email){
        users.values()
                .stream()
                .filter(usr -> usr.getFullName().equals(username)).findFirst()
                .ifPresent(usr -> usr.setUserName(username));

        users.values()
                .stream()
                .filter(usr -> usr.getEmail().equals(email))
                .findFirst().ifPresent(usr -> usr.setEmail(email));

        return true;
    }
}
