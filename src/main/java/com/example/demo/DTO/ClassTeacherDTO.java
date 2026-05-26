package com.example.demo.DTO;

import lombok.Data;

@Data
public class ClassTeacherDTO {
    private Long id;

    private Long classId;
    private String className;
    private String section;

    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    private String teacherQualification;
    private String teacherSpecialization;

    private String assignmentDate;
    private String roleType;
    private Boolean isPrimary;
    private String academicYear;
}