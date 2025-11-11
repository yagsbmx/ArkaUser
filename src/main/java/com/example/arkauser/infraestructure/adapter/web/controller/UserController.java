package com.example.arkauser.infraestructure.adapter.web.controller;

import com.example.arkauser.application.service.JwtService;
import com.example.arkauser.domain.model.User;
import com.example.arkauser.domain.model.enums.Rol;
import com.example.arkauser.domain.ports.in.UserUseCase;
import com.example.arkauser.infraestructure.client.ProductClient;
import com.example.arkauser.infraestructure.dto.ProductDto;
import com.example.arkauser.infraestructure.dto.UserRequestDto;
import com.example.arkauser.infraestructure.dto.UserResponseDto;
import com.example.arkauser.infraestructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase useCase;
    private final UserMapper mapper;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final ProductClient productClient;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto userRequestDto) {
        final String email = userRequestDto.getEmail() != null ? userRequestDto.getEmail().trim().toLowerCase() : null;
        final String username = userRequestDto.getUsername();

        var emailExists = email != null && useCase.getUserByEmail(email).isPresent();
        var usernameExists = username != null && useCase.getUserByUsername(username).isPresent();

        if (emailExists || usernameExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El usuario ya existe, no se pueden crear usuarios repetidos"));
        }

        User user = mapper.requestToDomain(userRequestDto);
        user.setEmail(email);

        // ❌ No encriptes aquí. Se encripta automáticamente en UserService
        var createdUser = useCase.createUser(user);
        UserResponseDto response = mapper.toResponseDto(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "El usuario se ha creado correctamente",
                        "data", response
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        final String rawIdentifier = body.get("email") != null && !body.get("email").isBlank()
                ? body.get("email").trim().toLowerCase()
                : (body.get("username") != null ? body.get("username").trim() : null);
        final String password = body.get("password");

        if (rawIdentifier == null || rawIdentifier.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Debe proporcionar usuario/email y contraseña"));
        }

        var userOpt = useCase.getUserByEmail(rawIdentifier)
                .or(() -> useCase.getUserByUsername(rawIdentifier));

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }

        var user = userOpt.get();
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }

        String token = jwtService.generateToken(
                user.getEmail() != null ? user.getEmail() : user.getUsername(),
                user.getId(),
                Rol.valueOf(user.getRole().toUpperCase())
        );

        return ResponseEntity.ok(Map.of(
                "message", "Inicio de sesión exitoso",
                "access_token", token
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        var u = useCase.getUserById(id);
        var response = mapper.toResponseDto(u);
        return ResponseEntity.ok(Map.of(
                "message", "Usuario consultado correctamente",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        var users = useCase.getAllUsers();
        var response = users.stream().map(mapper::toResponseDto).toList();
        return ResponseEntity.ok(Map.of(
                "message", "Listado de usuarios obtenido correctamente",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UserRequestDto userRequestDto) {
        var user = mapper.requestToDomain(userRequestDto);
        var updatedUser = useCase.updateUser(id, user);
        var response = mapper.toResponseDto(updatedUser);
        return ResponseEntity.ok(Map.of(
                "message", "El usuario se ha actualizado correctamente",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        useCase.deleteUser(id);
        return ResponseEntity.ok(Map.of(
                "message", "El usuario se eliminó satisfactoriamente"
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto> getProductFromProductService(@PathVariable Long id) {
        return ResponseEntity.ok(productClient.getProductById(id));
    }
}
