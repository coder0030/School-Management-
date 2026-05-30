package com.example.demo.SecurityTesting;

import com.example.demo.Controller.AuthController;
import com.example.demo.DTO.LoginResponseDTO;
import com.example.demo.DTO.SignupResponseDTO;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Role;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.LoginRequestDTO;
import com.example.demo.RequestDTO.SignupRequestDTO;
import com.example.demo.Security.AuthService;
import com.example.demo.Security.AuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.*;

import java.util.*;

import org.junit.jupiter.api.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AuthUtil authUtil;

    @MockitoBean
    private OAuth2User oAuth2User;

    @MockitoBean
    private UserRepository userRepository;

    private User user;
    private SignupRequestDTO signupRequest;
    private SignupResponseDTO signupResponse;
    private LoginRequestDTO loginRequest;
    private LoginResponseDTO loginResponse;
    private String jwtToken;

    @BeforeEach
    void setup() {

        jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ";

        user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .username("test@gmail.com")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        loginRequest = LoginRequestDTO.builder()
                .username("sumit@gmail.com")
                .password("1234")
                .build();

        loginResponse = LoginResponseDTO.builder()
                .id(1L)
                .jwt(jwtToken)
                .build();


        signupRequest = SignupRequestDTO.builder()
                .username("sumit@gmail.com")
                .password("1234")
                .build();

        signupResponse = SignupResponseDTO.builder()
                .id(1L)
                .username("sumit@gmail.com")
                .build();


    }

    @Test
    @Order(1)
    @DisplayName("Should return token when login is successful")
    void signup_Success() throws Exception {
        when(authService.signup(any(SignupRequestDTO.class))).thenReturn(signupResponse);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.username").value("sumit@gmail.com"));
    }

    @Test
    @Order(2)
    @DisplayName("Should return token when login is successful")
    void login_Success() throws Exception {

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ"))
                .andExpect(jsonPath("$.id").value(1));

        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when username is null")
    @Order(3)
    void login_UsernameNull() throws Exception {

        loginRequest.setUsername(null);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    @Order(4)
    @DisplayName("Should return 400 when password is null")
    void login_PasswordNull() throws Exception {

        loginRequest.setPassword(null);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    @Order(5)
    @DisplayName("Should return 400 when username is blank")
    void signup_UsernameBlank() throws Exception {
        signupRequest.setUsername("");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).signup(any());
    }

    @Test
    @Order(6)
    @DisplayName("Should return 400 when password is blank")
    void signup_PasswordBlank() throws Exception {
        signupRequest.setPassword("");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    @DisplayName("Should return 400 when user already exists")
    void signup_UserAlreadyExists() throws Exception {

        when(authService.signup(any(SignupRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("User already exists"));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())   // 400
                .andExpect(jsonPath("$.message").value("User already exists"));

        verify(authService, times(1)).signup(any(SignupRequestDTO.class));
    }

    @Test
    @DisplayName("Should create admin user when username contains @admin.com")
    @Order(8)
    void signup_AdminUser() throws Exception {
        SignupRequestDTO signupRequest = new SignupRequestDTO();
        signupRequest.setUsername("admin@admin.com");
        signupRequest.setPassword("admin123");

        SignupResponseDTO expectedResponse = new SignupResponseDTO(1L, "admin@admin.com");

        when(authService.signup(any(SignupRequestDTO.class))).thenReturn(expectedResponse);
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin@admin.com"));
    }
}
