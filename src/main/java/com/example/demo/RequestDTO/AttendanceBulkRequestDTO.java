package com.example.demo.RequestDTO;

import com.example.demo.Helper.AttendanceStatus;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceBulkRequestDTO {
    private Long teacherId;
    private Long subjectId;
    private Long classId;
    private String date;
    private Set<Long> studentIds;
    private String status;
}