package com.example.demo.Service;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.RequestDTO.StudentRequestDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public interface StudentService {
    StudentDTO createStudent(@Valid StudentRequestDTO studentRequestDTO);

    void deleteStudentById(Long id);

    StudentDTO updateStudentById(Long id, @Valid StudentRequestDTO studentRequestDTO) throws AccessDeniedException;

    StudentDTO patchUpdateStudentById(Long id, @Valid StudentRequestDTO studentRequestDTO) throws AccessDeniedException;

    List<StudentDTO> getAllStudent();

    StudentDTO getStudentById(Long id);

    StudentDTO assignStudentToClass(Long studentId, @Valid StudentRequestDTO requestDto);

    List<StudentDTO> getAllClassStudents();

    void deleteClassStudent(Long id);

    StudentDTO getClassStudentById(Long id);

    void removeStudentFromClass(Long classId, Long studentId);

    List<StudentDTO> getStudentsByClassId(Long classId);

    List<StudentDTO> getClassesByStudentId(Long studentId);

    List<StudentDTO> getAllStudents(Long classId);
}
