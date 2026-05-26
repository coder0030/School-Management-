package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSubjectDTO {

    private Long id;
    private Long classId;
    private Long subjectId;
    private Long teacherId;
    private LocalDate scheduleDay;
    private LocalTime scheduleTime;

}
