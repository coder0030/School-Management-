package com.example.demo.Mapper;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.DTO.TeacherDTO;
import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.RequestDTO.StudentRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {
    public StudentDTO toDto(Student student) {
        if(student == null) return null;


        StudentDTO studentDTO = StudentDTO.builder()
                .id(student.getId())
                .email(student.getEmail())
                .phone(student.getPhone())
                .address(student.getAddress())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .admissionDate(student.getAdmissionDate())
                .previousSchool(student.getPrevious_school())
                .status(student.getStatus())
                .rollNo(student.getRollNo())
                .build();

        return studentDTO;
    }

    public Student toEntity(StudentRequestDTO studentDTO, Student student) {
        if (studentDTO == null) return null;

        if (studentDTO.getEmail() != null) {
            student.setEmail(studentDTO.getEmail());
        }
        if (studentDTO.getPhone() != null) {
            student.setPhone(studentDTO.getPhone());
        }
        if (studentDTO.getAddress() != null) {
            student.setAddress(studentDTO.getAddress());
        }
        if (studentDTO.getFirstName() != null) {
            student.setFirstName(studentDTO.getFirstName());
        }
        if (studentDTO.getLastName() != null) {
            student.setLastName(studentDTO.getLastName());
        }
        if (studentDTO.getDateOfBirth() != null) {
            student.setDateOfBirth(studentDTO.getDateOfBirth());
        }
        if (studentDTO.getGender() != null) {
            student.setGender(studentDTO.getGender());
        }
        if (studentDTO.getPrevious_school() != null) {
            student.setPrevious_school(studentDTO.getPrevious_school());
        }
        if (studentDTO.getStatus() != null) {
            student.setStatus(studentDTO.getStatus());
        }

        return student;
    }

    public List<StudentDTO> toDtoList(List<Student> students) {
        if (students.isEmpty()) return null;
        return students.stream().map(this::toDto).toList();

    }

    public Student UpdateToEntity(StudentRequestDTO studentDTO,Student student) {
        boolean nullValue = false;

        if (studentDTO.getEmail() == null) nullValue = true;
        if (studentDTO.getPhone() == null) nullValue = true;
        if (studentDTO.getAddress() == null) nullValue = true;
        if (studentDTO.getFirstName() == null) nullValue = true;
        if (studentDTO.getLastName() == null) nullValue = true;
        if (studentDTO.getDateOfBirth() == null) nullValue = true;
        if (studentDTO.getGender() == null) nullValue = true;
        if (studentDTO.getPrevious_school() == null) nullValue = true;
        if (studentDTO.getStatus() == null) nullValue = true;

        if(nullValue == true) {
            throw new BadRequestException("Incomplete data provided.");
        }

        student.setEmail(studentDTO.getEmail());
        student.setPhone(studentDTO.getPhone());
        student.setAddress(studentDTO.getAddress());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setDateOfBirth(studentDTO.getDateOfBirth());
        student.setGender(studentDTO.getGender());
        student.setPrevious_school(studentDTO.getPrevious_school());
        student.setStatus(studentDTO.getStatus());

        return student;
    }
}
