package com.example.demo.RequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectRequestDTO {

    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Integer theoryMarks;
    private Integer practicalMarks;
}