package org.example.Repository;

import org.example.Domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

  void save(User user);
  Optional<User> findByEmail(String email);
  List<User> findAll();

}
