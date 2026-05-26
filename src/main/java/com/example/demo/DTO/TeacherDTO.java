package com.example.demo.DTO;

import com.example.demo.Helper.Role;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class TeacherDTO extends PersonDTO {

    private Long id;

    private LocalDateTime localDateTime;

    private String qualification;

    private String specialization;

    private Role role;
}