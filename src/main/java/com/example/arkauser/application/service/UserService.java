package com.example.arkauser.application.service;

import com.example.arkauser.domain.model.User;
import com.example.arkauser.domain.model.enums.Rol;
import com.example.arkauser.domain.ports.in.UserUseCase;
import com.example.arkauser.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        String email = user.getEmail() != null ? user.getEmail().trim().toLowerCase() : null;
        String encodedPassword = user.getPassword() != null ? passwordEncoder.encode(user.getPassword()) : null;

        String desired = user.getRole() != null ? user.getRole() : Rol.USER.name();
        Rol finalEnum = toEnum(desired);

        User toCreate = new User(
                null,
                user.getUsername(),
                email,
                encodedPassword,
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.isActive(),
                user.getCountry(),
                user.getCity(),
                finalEnum.name()
        );
        return userRepositoryPort.createUser(toCreate);
    }

    @Override
    public User getUserById(Long id) {
        return userRepositoryPort.getUserById(id);
    }

    @Override
    public User updateUser(Long id, User user) {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");
        if (user == null) throw new IllegalArgumentException("El payload de usuario no puede ser nulo");
        if (user.getId() != null && !id.equals(user.getId())) throw new IllegalArgumentException("El ID del usuario no coincide con el ID proporcionado");

        User current = userRepositoryPort.getUserById(id);

        String newEmail = user.getEmail() != null ? user.getEmail().trim().toLowerCase() : current.getEmail();
        String newPassword = user.getPassword() != null && !user.getPassword().isBlank()
                ? passwordEncoder.encode(user.getPassword())
                : current.getPassword();

        String incomingRole = user.getRole() != null ? user.getRole() : current.getRole();
        String normalizedIncoming = toEnum(incomingRole).name();

        User toUpdate = new User(
                id,
                user.getUsername() != null ? user.getUsername() : current.getUsername(),
                newEmail,
                newPassword,
                user.getFirstName() != null ? user.getFirstName() : current.getFirstName(),
                user.getLastName() != null ? user.getLastName() : current.getLastName(),
                user.getPhoneNumber() != null ? user.getPhoneNumber() : current.getPhoneNumber(),
                user.getAddress() != null ? user.getAddress() : current.getAddress(),
                user.isActive(),
                user.getCountry() != null ? user.getCountry() : current.getCountry(),
                user.getCity() != null ? user.getCity() : current.getCity(),
                normalizedIncoming
        );

        validateNonBlankIfPresent(toUpdate);
        return userRepositoryPort.updateUser(id, toUpdate);
    }

    @Override
    public void deleteUser(Long id) {
        userRepositoryPort.deleteUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.getAllUsers();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepositoryPort.getUserByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepositoryPort.getUserByEmail(email != null ? email.trim().toLowerCase() : null);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepositoryPort.getUsersByRole(role);
    }

    @Override
    public List<User> getUsersByActiveStatus(boolean isActive) {
        return userRepositoryPort.getUsersByActiveStatus(isActive);
    }

    private Rol toEnum(String value) {
        if (value == null) return Rol.USER;
        try { return Rol.valueOf(value.toUpperCase()); } catch (IllegalArgumentException e) { return Rol.USER; }
    }

    private void validateNonBlankIfPresent(User u) {
        if (u.getUsername() != null && u.getUsername().isBlank()) throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        if (u.getEmail() != null && u.getEmail().isBlank()) throw new IllegalArgumentException("El correo electrónico no puede estar vacío");
        if (u.getPassword() != null && u.getPassword().isBlank()) throw new IllegalArgumentException("La contraseña no puede estar vacía");
        if (u.getRole() != null && u.getRole().isBlank()) throw new IllegalArgumentException("El rol no puede estar vacío");
        if (u.getAddress() != null && u.getAddress().isBlank()) throw new IllegalArgumentException("La dirección no puede estar vacía");
        if (u.getFirstName() != null && u.getFirstName().isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío");
        if (u.getLastName() != null && u.getLastName().isBlank()) throw new IllegalArgumentException("El apellido no puede estar vacío");
        if (u.getPhoneNumber() != null && u.getPhoneNumber().isBlank()) throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
        if (u.getCountry() != null && u.getCountry().isBlank()) throw new IllegalArgumentException("El país no puede estar vacío");
        if (u.getCity() != null && u.getCity().isBlank()) throw new IllegalArgumentException("La ciudad no puede estar vacía");
    }
}
