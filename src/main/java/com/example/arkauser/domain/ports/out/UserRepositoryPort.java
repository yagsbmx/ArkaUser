package com.example.arkauser.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.example.arkauser.domain.model.User;

public interface UserRepositoryPort {

    User createUser(User user);
    User getUserById(Long id);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    List<User> getAllUsers();
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> getUsersByRole(String role);
    List<User> getUsersByActiveStatus(boolean isActive);
}
