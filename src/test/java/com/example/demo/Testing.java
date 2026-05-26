package com.example.demo;

import com.example.demo.DTO.SignupResponseDTO;
import com.example.demo.DTO.TeacherDTO;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Gender;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.Mapper.TeacherMapper;
import com.example.demo.MyException.DuplicateResourceException;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.SignupRequestDTO;
import com.example.demo.RequestDTO.TeacherRequestDTO;
import com.example.demo.Security.AuthService;
import com.example.demo.ServiceImpl.TeacherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest // ✓ Add this for Mockito support
@RequiredArgsConstructor
public class Testing {

    private AuthService authService;
    private UserRepository userRepository;

    @Test
   public void test() {
        SignupRequestDTO signupRequest = SignupRequestDTO.builder()
                .username("sumit")
                .password("1234")
                .build();

        SignupResponseDTO signupResponseDTO = authService.signup(signupRequest);
        if(userRepository.existsByUsername(signupRequest.getUsername())) {
            System.out.println("saved");
        }
   }
}