package com.example.demo.Security;

import com.example.demo.DTO.LoginResponseDTO;
import com.example.demo.DTO.SignupResponseDTO;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.AuthProviderType;
import com.example.demo.Helper.Role;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.LoginRequestDTO;
import com.example.demo.RequestDTO.SignupRequestDTO;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    public LoginResponseDTO login(@Valid LoginRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.getUsername().toLowerCase();
        Authentication authentication = authenticationManager.authenticate (

                new UsernamePasswordAuthenticationToken(username, loginRequestDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = authil.generateAccessToken(user);
        return new LoginResponseDTO(token, user.getId());
    }

    public SignupResponseDTO signup(@Valid SignupRequestDTO signupRequestDTO) {

        if(signupRequestDTO.getUsername() == null || signupRequestDTO.getUsername().isBlank()
        || signupRequestDTO.getPassword() == null || signupRequestDTO.getPassword().isBlank()) {
            throw new BadRequestException("UserName or Password can't be null.");
        }

        if (userRepository.findByUsername(signupRequestDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        String username = signupRequestDTO.getUsername().toLowerCase();
        Set<Role> roles;
        if (username.contains("@admin.com")) {
            roles = Set.of(Role.ROLE_ADMIN, Role.ROLE_USER);

        } else if(username.contains(("@superadmin.com"))) {
            roles = Set.of(Role.ROLE_SUPERADMIN, Role.ROLE_USER);

        } else {
            roles = Set.of(Role.ROLE_USER);
        }

        User user = userRepository.save(
                User.builder()
                        .username(signupRequestDTO.getUsername())
                        .password(passwordEncoder.encode(signupRequestDTO.getPassword()))
                        .roles(roles)
                        .providerType(AuthProviderType.LOCAL)
                        .build()
        );

        return new SignupResponseDTO(user.getId(), user.getUsername());
    }
}
