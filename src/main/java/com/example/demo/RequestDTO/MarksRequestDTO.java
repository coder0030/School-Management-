package com.example.demo.RequestDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarksRequestDTO {
    private Long studentId;
    private Long subjectId;
    private Long examId;
    private Double marksObtained;
    private String grade;
}
