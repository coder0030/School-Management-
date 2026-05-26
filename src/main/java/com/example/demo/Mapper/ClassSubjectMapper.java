package com.example.demo.Mapper;

import com.example.demo.DTO.ClassSubjectDTO;
import com.example.demo.ENTITY.ClassSubject;
import com.example.demo.RequestDTO.ClassSubjectRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClassSubjectMapper {
    public ClassSubject toEntity(ClassSubjectDTO classSubjectDto) {
        if (classSubjectDto == null) return null;

        return ClassSubject.builder()
                .id(classSubjectDto.getId())
                .build();
    }

    public ClassSubjectDTO toDTO(ClassSubject classSubject) {
        if (classSubject == null) return null;

        return ClassSubjectDTO.builder()
                .id(classSubject.getId())
                .classId(classSubject.getClassEntity().getId())
                .subjectId(classSubject.getSubject().getId())
                .teacherId(classSubject.getTeacher().getId())
                .build();
    }

    public List<ClassSubjectDTO> classDTOList(List<ClassSubject> entities) {
        if (entities == null || entities.isEmpty()) return null;
        return entities.stream().map(this::toDTO).toList();
    }
}