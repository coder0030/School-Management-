package com.example.demo.RequestDTO;

import com.example.demo.Helper.Gender;
import com.example.demo.Helper.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherRequestDTO {

    private String firstName;

    private String lastName;

    @Email(message = "Email format is invalid")
    private String email;

    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phone;

    private String address;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String qualification;

    private String specialization;

    @Enumerated(EnumType.STRING)
    private Role role;
}