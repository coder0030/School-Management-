package com.example.demo.DTO;

import com.example.demo.Helper.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private Long timetableId;
    private String subjectName;
    private String teacherName;
    private String className;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private LocalDateTime updatedAt;
    private List<Long> notSavedAttendanceStudent;
}