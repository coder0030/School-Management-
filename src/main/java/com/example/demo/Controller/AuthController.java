package com.example.demo.Controller;

import com.example.demo.DTO.LoginResponseDTO;
import com.example.demo.DTO.SignupResponseDTO;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Role;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.LoginRequestDTO;
import com.example.demo.RequestDTO.SignupRequestDTO;
import com.example.demo.Security.AuthService;
import com.example.demo.Security.AuthUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponse = authService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequestDTO) {
        SignupResponseDTO signupResponse = authService.signup(signupRequestDTO);
        return ResponseEntity.ok(signupResponse);
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2Success(@AuthenticationPrincipal OAuth2User oAuth2User) {

        String email = oAuth2User.getAttribute("email");

        if(email == null) {
            String login = oAuth2User.getAttribute("login");
            email = login + "@github.com";
        }

        String finalEmail = email;

        User user = userRepository.findByEmailOrUsernameAndIsActive(email, email, true)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(finalEmail);
                    newUser.setUsername(oAuth2User.getAttribute("email") != null
                            ? oAuth2User.getAttribute("email")
                            : oAuth2User.getAttribute("login"));
                    newUser.setPassword("");
                    newUser.setRoles(Set.of(Role.ROLE_USER));
                    return userRepository.save(newUser);
                });

        String token = authUtil.generateAccessToken(user);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email
        ));
    }
}
