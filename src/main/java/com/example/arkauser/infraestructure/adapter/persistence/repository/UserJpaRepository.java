package com.example.arkauser.infraestructure.adapter.persistence.repository;

import com.example.arkauser.domain.model.enums.Rol;
import com.example.arkauser.infraestructure.adapter.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(Rol role);

    List<UserEntity> findByActive(boolean active);
}
