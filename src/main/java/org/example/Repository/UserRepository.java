package org.example.Repository;

import org.example.Domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void save(User user);
  Optional<User> findByEmail(String email);
  boolean updateProfile(String username , String email);
  boolean updatePassword(String password , UUID userId) ;

}
