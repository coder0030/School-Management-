package com.example.demo.DTO;

import com.example.demo.Helper.Gender;
import com.example.demo.Helper.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibrarianDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String adminCode;
    private Set<Role> roles;
    private String department;
    private Boolean isActive;
    private String fullName;
}
