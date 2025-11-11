package com.example.arkauser.infraestructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.arkauser.domain.model.User;
import com.example.arkauser.domain.model.enums.Rol;
import com.example.arkauser.domain.ports.out.UserRepositoryPort;
import com.example.arkauser.infraestructure.adapter.persistence.repository.UserJpaRepository;
import com.example.arkauser.infraestructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User createUser(User user) {
        return userMapper.toDomain(userJpaRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public User getUserById(Long id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User updateUser(Long id, User user) {
        return userJpaRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setRole(toEnum(user.getRole()));
                    existingUser.setActive(user.isActive());
                    return userMapper.toDomain(userJpaRepository.save(existingUser));
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userJpaRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userJpaRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userJpaRepository.findAll()
                .stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userJpaRepository.findByRole(toEnum(role))
                .stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> getUsersByActiveStatus(boolean isActive) {
        return userJpaRepository.findByActive(isActive)
                .stream()
                .map(userMapper::toDomain)
                .toList();
    }

    private Rol toEnum(String value) {
        if (value == null) return Rol.USER;
        try {
            return Rol.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Rol.USER;
        }
    }
}
