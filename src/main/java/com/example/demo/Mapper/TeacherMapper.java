package com.example.demo.Mapper;

import com.example.demo.DTO.TeacherDTO;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.RequestDTO.TeacherRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeacherMapper {

    public TeacherDTO toDto(Teacher teacher) {
        if (teacher == null) return null;

        return TeacherDTO.builder()
                .id(teacher.getId())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .address(teacher.getAddress())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .dateOfBirth(teacher.getDateOfBirth())
                .gender(teacher.getGender())
                .specialization(teacher.getSpecialization())
                .qualification(teacher.getQualification())
                .localDateTime(teacher.getLocalDateTime())
                .role(teacher.getRole())
                .build();
    }

    public Teacher UpdateToEntity(TeacherRequestDTO teacherDTO, Teacher teacher) {
        if (teacherDTO == null) {
            throw new IllegalArgumentException("Request body is null");
        }

        if (teacherDTO.getEmail() == null ||
                teacherDTO.getPhone() == null ||
                teacherDTO.getAddress() == null ||
                teacherDTO.getFirstName() == null ||
                teacherDTO.getLastName() == null ||
                teacherDTO.getDateOfBirth() == null ||
                teacherDTO.getGender() == null ||
                teacherDTO.getSpecialization() == null ||
                teacherDTO.getQualification() == null ||
                teacherDTO.getRole() == null) {
            throw new IllegalArgumentException("Incomplete request body data");
        }

        teacher.setEmail(teacherDTO.getEmail());
        teacher.setPhone(teacherDTO.getPhone());
        teacher.setAddress(teacherDTO.getAddress());
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setDateOfBirth(teacherDTO.getDateOfBirth());
        teacher.setGender(teacherDTO.getGender());
        teacher.setSpecialization(teacherDTO.getSpecialization());
        teacher.setQualification(teacherDTO.getQualification());
        teacher.setRole(teacherDTO.getRole());

        return teacher;
    }

    public Teacher updateEntityFromRequest(Teacher teacher, TeacherRequestDTO teacherDTO) {
        if (teacherDTO == null) {
            throw new IncompleteDataException("Teacher data is missing");
        }
        if (teacher == null) {
            throw new IncompleteDataException("Teacher entity is null");
        }
        if (teacherDTO.getFirstName() != null) teacher.setFirstName(teacherDTO.getFirstName());
        if (teacherDTO.getLastName() != null) teacher.setLastName(teacherDTO.getLastName());
        if (teacherDTO.getEmail() != null) teacher.setEmail(teacherDTO.getEmail());
        if (teacherDTO.getPhone() != null) teacher.setPhone(teacherDTO.getPhone());
        if (teacherDTO.getAddress() != null) teacher.setAddress(teacherDTO.getAddress());
        if (teacherDTO.getDateOfBirth() != null) teacher.setDateOfBirth(teacherDTO.getDateOfBirth());
        if (teacherDTO.getGender() != null) teacher.setGender(teacherDTO.getGender());
        if (teacherDTO.getQualification() != null) teacher.setQualification(teacherDTO.getQualification());
        if (teacherDTO.getSpecialization() != null) teacher.setSpecialization(teacherDTO.getSpecialization());

        return teacher;
    }


    public List<TeacherDTO> toDtoList(List<Teacher> teacherList) {
        if (teacherList == null) return null;
        return teacherList.stream().map(this::toDto).collect(Collectors.toList());
    }
}