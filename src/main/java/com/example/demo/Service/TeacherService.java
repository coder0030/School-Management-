package com.example.demo.Service;

import com.example.demo.DTO.TeacherDTO;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.TeacherRequestDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface TeacherService {
    TeacherDTO createTeacher(@Valid TeacherRequestDTO teacherRequestDTO);

    TeacherDTO updateTeacher(Long teacherId, @Valid TeacherRequestDTO requestDTO);

    void deleteTeacher(Long teacherId);

    TeacherDTO getTeacherById(Long teacherId);

    TeacherDTO partialUpdate(Long teacherId, TeacherRequestDTO requestDTO);

    List<TeacherDTO> getAllTeacher();

    List<TeacherDTO> getTeacherByClass(Long classId);

    void assignTeacherSubjectToClass(Long teacherId, Long subjectId, Long classId);

    void removeTeacherSubjectToClass(Long teacherId, Long subjectId, Long classId);

    TeacherDTO addRoleToTeacher(Long teacherId, Role role);

    TeacherDTO removeRoleFromTeacher(Long teacherId, Role role);
}
