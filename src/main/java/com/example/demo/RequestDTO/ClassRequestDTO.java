package com.example.demo.RequestDTO;

import com.example.demo.DTO.TeacherDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRequestDTO {

    private String className;

    private String section;

    private String academicYear;

    private String roomNo;

    private Integer capacity;
}
