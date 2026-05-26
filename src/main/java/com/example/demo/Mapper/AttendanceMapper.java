package com.example.demo.Mapper;

import com.example.demo.DTO.AttendanceDTO;
import com.example.demo.ENTITY.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttendanceMapper {

    public Attendance toEntity(AttendanceDTO attendanceDTO, Student student, Timetable timetable) {
        if (attendanceDTO == null) {
            return null;
        }

        return Attendance.builder()
                .id(attendanceDTO.getId())
                .student(student)
                .timetable(timetable)
                .attendanceDate(attendanceDTO.getAttendanceDate())
                .status(attendanceDTO.getStatus())
                .updatedAt(attendanceDTO.getUpdatedAt())
                .build();
    }

    public AttendanceDTO toDTO(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        Student student = attendance.getStudent();
        Timetable timetable = attendance.getTimetable();

        Subject subject = timetable != null ? timetable.getSubject() : null;
        Teacher teacher = timetable != null ? timetable.getTeacher() : null;
        ClassEntity classEntity = timetable != null ? timetable.getClassEntity() : null;

        return AttendanceDTO.builder()
                .id(attendance.getId())
                .studentId(student != null ? student.getId() : null)
                .studentName(student != null ? student.getStudentName() : null)
                .studentRollNumber(student != null ? String.valueOf(student.getRollNo()) : null)
                .timetableId(timetable != null ? timetable.getId() : null)
                .subjectName(subject != null ? subject.getSubjectName() : null)
                .teacherName(teacher != null ? teacher.getTeacherName() : null)
                .className(classEntity != null ? classEntity.getClassName() : null)
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }

    public List<AttendanceDTO> toDTOList(List<Attendance> attendances) {
        if (attendances == null) {
            return null;
        }
        return attendances.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntity(Attendance attendance, AttendanceDTO attendanceDTO, Student student, Timetable timetable) {
        if (attendanceDTO == null) {
            return;
        }

        if (student != null) {
            attendance.setStudent(student);
        }
        if (timetable != null) {
            attendance.setTimetable(timetable);
        }
        if (attendanceDTO.getAttendanceDate() != null) {
            attendance.setAttendanceDate(attendanceDTO.getAttendanceDate());
        }
        if (attendanceDTO.getStatus() != null) {
            attendance.setStatus(attendanceDTO.getStatus());
        }
    }
}