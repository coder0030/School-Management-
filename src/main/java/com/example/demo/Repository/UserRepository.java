package com.example.demo.Repository;

import com.example.demo.ENTITY.User;
import com.example.demo.RequestDTO.SignupRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(@Valid String Username);

    Optional<User> findByProviderIdAndProviderType(String providerId, String s);

    Optional<User> findByEmailOrUsernameAndIsActive(String email, String emailOrUsername, boolean isActive);

    Optional<User> findByUsernameAndIsActive(String username, boolean isActive);
}
