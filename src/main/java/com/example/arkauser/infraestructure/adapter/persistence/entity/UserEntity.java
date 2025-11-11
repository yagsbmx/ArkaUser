package com.example.arkauser.infraestructure.adapter.persistence.entity;

import com.example.arkauser.domain.model.enums.Rol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name="uk_users_username", columnNames="username"),
           @UniqueConstraint(name="uk_users_email", columnNames="email")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;

    @Column(nullable = false)
    private boolean active = true;

    private String country;
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol role;
}

