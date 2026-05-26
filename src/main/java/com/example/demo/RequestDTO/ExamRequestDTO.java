package com.example.demo.RequestDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRequestDTO {
    private String examName;
    private String examType;
    private String academicYear;
    private int semester;
    private LocalDate startDate;
    private LocalDate endDate;
    private int maxMarks;
    private int passingMarks;
    private Long classId;
}
