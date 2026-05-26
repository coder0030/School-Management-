package com.example.demo.Service;

import com.example.demo.DTO.ClassTeacherDTO;
import com.example.demo.RequestDTO.ClassTeacherRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClassTeacherService {

    ClassTeacherDTO assignTeacherToClass(@Valid ClassTeacherRequestDTO requestDto);

    ClassTeacherDTO getClassTeacherById(Long id);

    void deleteClassTeacher(Long id);

    void removeTeacherFromClass(Long classId, Long teacherId);

    List<ClassTeacherDTO> getTeachersByClassId(Long classId);

    List<ClassTeacherDTO> getClassesByTeacherId(Long teacherId);

    Page<ClassTeacherDTO> getAllClassTeachers(int pageNo, int pageSize);
}