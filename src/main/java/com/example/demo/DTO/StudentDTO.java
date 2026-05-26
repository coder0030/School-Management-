package com.example.demo.DTO;

import com.example.demo.Helper.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO extends PersonDTO {

    private Long id;

    @NotNull(message = "Roll number is required")
    @Positive(message = "Roll number must be positive")
    private Integer rollNo;

    @NotNull(message = "Admission date is required")
    @PastOrPresent(message = "Admission date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate;

    @NotNull(message = "Student status is required")
    private Status status;

    @Size(max = 255, message = "Previous school name cannot exceed 255 characters")
    private String previousSchool;
    private Long classId;
    private String className;
    private String classSection;
}