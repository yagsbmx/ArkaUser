package com.example.arkauser.infraestructure.mapper;

import com.example.arkauser.domain.model.User;
import com.example.arkauser.domain.model.enums.Rol;
import com.example.arkauser.infraestructure.adapter.persistence.entity.UserEntity;
import com.example.arkauser.infraestructure.dto.UserRequestDto;
import com.example.arkauser.infraestructure.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getAddress(),
                entity.isActive(),
                entity.getCountry(),
                entity.getCity(),
                entity.getRole() != null ? entity.getRole().name() : null
        );
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .active(user.isActive())
                .country(user.getCountry())
                .city(user.getCity())
                .role(toEnum(user.getRole()))
                .build();
    }

    public UserResponseDto toResponseDto(User user) {
        if (user == null) return null;
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.isActive(),
                user.getCountry(),
                user.getCity(),
                user.getRole()
        );
    }

    public User requestToDomain(UserRequestDto requestDto) {
        if (requestDto == null) return null;
        return new User(
                requestDto.getId(),
                requestDto.getUsername(),
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getPhoneNumber(),
                requestDto.getAddress(),
                requestDto.getActive() != null ? requestDto.getActive() : true,
                requestDto.getCountry(),
                requestDto.getCity(),
                requestDto.getRole() != null ? requestDto.getRole() : "USER"
        );
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
