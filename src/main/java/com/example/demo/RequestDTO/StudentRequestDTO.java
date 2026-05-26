package com.example.demo.RequestDTO;

import com.example.demo.Helper.Gender;
import com.example.demo.Helper.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequestDTO {
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private LocalDate dateOfBirth;

    private Gender gender;

    private LocalDate localDate;

    private Status status;

    private String previous_school;

    private String specialization;

    private Long classId;
}
