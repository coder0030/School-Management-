package com.example.demo.RequestDTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TeacherSubjectRequestDTO {
    private Long subjectId;
    private Long teacherId;
}
