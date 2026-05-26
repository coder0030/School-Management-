package com.example.demo.DTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {

    private Long id;
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Integer theoryMarks;
    private Integer practicalMarks;
    private Long courseId;
}