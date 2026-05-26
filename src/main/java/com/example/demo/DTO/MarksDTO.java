package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class MarksDTO {
    private Long id;
    private Long studentId;
    private Long subjectId;
    private Long examId;
    private Double marksObtained;
    private Double averageMarks;
 //   private List<MarksSubjectDTO> marksList;
    private String grade;
}
