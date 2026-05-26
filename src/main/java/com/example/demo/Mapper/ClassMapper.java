package com.example.demo.Mapper;

import com.example.demo.DTO.ClassDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.RequestDTO.ClassRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClassMapper {

    private final ClassRepository classRepository;

    public ClassEntity partialToEntity(ClassRequestDTO classDTO, ClassEntity classEntity) {
        if (classDTO == null || classEntity == null) return null;

        if (classDTO.getClassName() != null) {
            classEntity.setClassName(classDTO.getClassName().toUpperCase());
        }
        if (classDTO.getAcademicYear() != null) {
            classEntity.setAcademicYear(classDTO.getAcademicYear());
        }
        if (classDTO.getSection() != null) {
            classEntity.setSection(classDTO.getSection().toLowerCase());
        }
        if (classDTO.getRoomNo() != null) {
            classEntity.setRoomNo(classDTO.getRoomNo());
        }
        if (classDTO.getCapacity() != null) {
            classEntity.setCapacity(classDTO.getCapacity());
        }

        return classEntity;
    }

    public ClassDTO toDTO(ClassEntity classEntity) {
        if (classEntity == null) return null;

        return ClassDTO.builder()
                .id(classEntity.getId())
                .className(classEntity.getClassName().toUpperCase())
                .section(classEntity.getSection().toLowerCase())
                .academicYear(classEntity.getAcademicYear())
                .roomNo(classEntity.getRoomNo())
                .capacity(classEntity.getCapacity())
                .build();
    }

    public ClassEntity requestToEntity(ClassEntity classEntity, ClassRequestDTO classRequestDTO) {
        if (classRequestDTO == null) return null;

        boolean isAnyFieldNull = false;

        if (classRequestDTO.getClassName() == null) isAnyFieldNull = true;
        if (classRequestDTO.getSection() == null) isAnyFieldNull = true;
        if (classRequestDTO.getAcademicYear() == null) isAnyFieldNull = true;
        if (classRequestDTO.getRoomNo() == null) isAnyFieldNull = true;
        if (classRequestDTO.getCapacity() == null) isAnyFieldNull = true;

        if (isAnyFieldNull) {
            throw new RuntimeException("Class data is incomplete");
        }

            classEntity.setClassName(classRequestDTO.getClassName().toUpperCase());
            classEntity.setSection(classRequestDTO.getSection().toLowerCase());
            classEntity.setAcademicYear(classRequestDTO.getAcademicYear());
            classEntity.setRoomNo(classRequestDTO.getRoomNo());
            classEntity.setCapacity(classRequestDTO.getCapacity());

        return classEntity;
    }

    public List<ClassDTO> classDTOList(List<ClassEntity> classEntityList) {
        if(classEntityList.isEmpty()) return null;

        return classEntityList.stream().map(this::toDTO).toList();
    }
}
