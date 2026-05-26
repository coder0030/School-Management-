package com.example.demo.Mapper;

import com.example.demo.DTO.ClassTeacherDTO;
import com.example.demo.ENTITY.ClassTeacher;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.ClassTeacherRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClassTeacherMapper {

    public ClassTeacherDTO toDTO(ClassTeacher classTeacher) {
        if (classTeacher == null) {
            return null;
        }

        ClassTeacherDTO dto = new ClassTeacherDTO();
        dto.setId(classTeacher.getId());

        dto.setClassId(classTeacher.getClassEntity().getId());
        dto.setClassName(classTeacher.getClassEntity().getClassName());
        dto.setSection(classTeacher.getClassEntity().getSection());
        dto.setTeacherId(classTeacher.getTeacher().getId());
        dto.setTeacherEmail(classTeacher.getTeacher().getEmail());
        dto.setTeacherQualification(classTeacher.getTeacher().getQualification());
        dto.setTeacherSpecialization(classTeacher.getTeacher().getSpecialization());
        dto.setAssignmentDate(String.valueOf(classTeacher.getAssignmentDate()));
        dto.setRoleType(String.valueOf(classTeacher.getRoleType()));
        dto.setIsPrimary(classTeacher.getIsPrimary());
        dto.setAcademicYear(classTeacher.getAcademicYear());
        dto.setTeacherName(classTeacher.getTeacher().getTeacherName());
        return dto;
    }

    public List<ClassTeacherDTO> toDTOList(List<ClassTeacher> classTeachers) {
        if (classTeachers == null) {
            return null;
        }
        return classTeachers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClassTeacher toEntity(ClassTeacherDTO dto) {
        if (dto == null) {
            return null;
        }

        ClassTeacher classTeacher = new ClassTeacher();
        classTeacher.setId(dto.getId());
        classTeacher.setAssignmentDate(LocalDate.parse(dto.getAssignmentDate()));
        classTeacher.setIsPrimary(dto.getIsPrimary());
        classTeacher.setAcademicYear(dto.getAcademicYear());

        return classTeacher;
    }
}