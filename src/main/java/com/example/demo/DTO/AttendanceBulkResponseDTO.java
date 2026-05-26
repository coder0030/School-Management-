package com.example.demo.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceBulkResponseDTO {

    private int savedCount;
    private List<Long> skippedStudentIds;
    private String message;

}