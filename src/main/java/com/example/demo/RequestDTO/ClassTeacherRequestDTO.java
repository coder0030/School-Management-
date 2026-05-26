package com.example.demo.RequestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassTeacherRequestDTO {
    @NotNull(message = "Class ID cannot be null")
    private Long classId;

    @NotNull(message = "Teacher ID cannot be null")
    private Long teacherId;
}