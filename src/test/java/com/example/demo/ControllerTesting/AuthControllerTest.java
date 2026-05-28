package com.example.demo.ControllerTesting;

import com.example.demo.Controller.AuthController;
import com.example.demo.DTO.LoginResponseDTO;
import com.example.demo.DTO.SignupResponseDTO;
import com.example.demo.RequestDTO.LoginRequestDTO;
import com.example.demo.RequestDTO.SignupRequestDTO;
import com.example.demo.Security.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    private com.example.demo.Repository.UserRepository userRepository;

    @MockitoBean
    private com.example.demo.Security.AuthUtil authUtil;

    private SignupRequestDTO validSignupRequest;
    private LoginRequestDTO validLoginRequest;
    private SignupResponseDTO signupResponse;
    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        validSignupRequest = SignupRequestDTO.builder()
                .username("sumit@gmail.com")
                .password("password123")
                .build();

        validLoginRequest = LoginRequestDTO.builder()
                .username("sumit@gmail.com")
                .password("password123")
                .build();

        signupResponse = SignupResponseDTO.builder()
                .id(1L)
                .username(validSignupRequest.getUsername()) // Use the actual username
                .build();

        loginResponse = LoginResponseDTO.builder()
                .jwt("Bearer eyJhbGciOiJIUzI1NiJ9.dummy.signature")
                .id(1L)
                .build();
    }

    @Test
    @Order(1)
    void signup_WithValidRequest_ShouldReturnCreatedUser() throws Exception {
        when(authService.signup(any(SignupRequestDTO.class)))
                .thenReturn(signupResponse);

        ResultActions result = mockMvc.perform(
                post("/public/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignupRequest))
                        .with(csrf())
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("sumit@gmail.com"));

        verify(authService, times(1)).signup(any(SignupRequestDTO.class));
    }

    @Test
    @Order(2)
    void signup_WithNullUsername_ShouldReturnBadRequest() throws Exception {
        SignupRequestDTO invalidRequest = SignupRequestDTO.builder()
                .username(null)
                .password("password123")
                .build();

        ResultActions result = mockMvc.perform(post("/public/auth/signup")
                .content(objectMapper.writeValueAsString(invalidRequest))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void signup_WithNullPassword_ShouldReturnBadRequest() throws Exception {
        SignupRequestDTO invalidRequest = SignupRequestDTO.builder()
                .username("testuser@gmail.com")
                .password(null)
                .build();

        ResultActions result = mockMvc.perform(post("/public/auth/signup")
                .content(objectMapper.writeValueAsString(invalidRequest))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
        verify(authService, never()).signup(any(SignupRequestDTO.class));
    }

    @Test
    @Order(4)
    void signup_WithEmptyUsername_ShouldReturnBadRequest() throws Exception {
        SignupRequestDTO invalidRequest = SignupRequestDTO.builder()
                .username("")
                .password("password123")
                .build();

        ResultActions result = mockMvc.perform(post("/public/auth/signup")
                .content(objectMapper.writeValueAsString(invalidRequest))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
        verify(authService, never()).signup(any(SignupRequestDTO.class));
    }
}