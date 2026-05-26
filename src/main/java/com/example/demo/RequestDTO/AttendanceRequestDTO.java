package com.example.demo.RequestDTO;

import com.example.demo.Helper.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequestDTO {
    private Long teacherId;
    private Long subjectId;
    private Long classId;
    private Long timetableId;  // ADD THIS - required for Attendance entity
    private LocalDate attendanceDate;
    private AttendanceStatus status;  // For single student attendance

    private Set<Long> studentIds = new HashSet<>();
}