package com.example.demo.RequestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassTeacherRequestDTO {
    @NotNull(message = "Class ID cannot be null")
    private Long classId;

    @NotNull(message = "Teacher ID cannot be null")
    private Long teacherId;
}