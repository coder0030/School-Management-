package com.example.demo.Mapper;
import com.example.demo.DTO.ParentResponseDTO;
import com.example.demo.ENTITY.Parent;
import com.example.demo.ENTITY.Student;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.RequestDTO.ParentRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParentMapper {

    public ParentResponseDTO toDto(Parent parent) {
        if (parent == null) return null;

        ParentResponseDTO parentDTO = ParentResponseDTO.builder()
                .id(parent.getId())
                .firstName(parent.getFirstName())
                .lastName(parent.getLastName())
                .email(parent.getEmail())
                .phone(parent.getPhone())
                .address(parent.getAddress())
                .dateOfBirth(parent.getDateOfBirth() != null ? parent.getDateOfBirth().toString() : null)
                .gender(parent.getGender())
                .occupation(parent.getOccupation())
                .userId(parent.getUser() != null ? parent.getUser().getId() : null)
                .studentIds(parent.getStudentList().stream().map(Student::getId).collect(Collectors.toList()))
                .build();

        return parentDTO;
    }

    public Parent toEntity(ParentRequestDTO parentDTO, Parent parent) {
        if (parentDTO == null) return null;

        if (parentDTO.getEmail() != null) {
            parent.setEmail(parentDTO.getEmail());
        }
        if (parentDTO.getPhone() != null) {
            parent.setPhone(parentDTO.getPhone());
        }
        if (parentDTO.getAddress() != null) {
            parent.setAddress(parentDTO.getAddress());
        }
        if (parentDTO.getFirstName() != null) {
            parent.setFirstName(parentDTO.getFirstName());
        }
        if (parentDTO.getLastName() != null) {
            parent.setLastName(parentDTO.getLastName());
        }
        if (parentDTO.getDateOfBirth() != null) {
            parent.setDateOfBirth(parentDTO.getDateOfBirth());
        }
        if (parentDTO.getGender() != null) {
            parent.setGender(parentDTO.getGender());
        }
        if (parentDTO.getOccupation() != null) {
            parent.setOccupation(parentDTO.getOccupation());
        }

        return parent;
    }

    public List<ParentResponseDTO> toDtoList(List<Parent> parents) {
        if (parents == null || parents.isEmpty()) return null;
        return parents.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Parent updateToEntity(ParentRequestDTO parentDTO, Parent parent) {
        boolean nullValue = false;

        if (parentDTO.getEmail() == null) nullValue = true;
        if (parentDTO.getPhone() == null) nullValue = true;
        if (parentDTO.getAddress() == null) nullValue = true;
        if (parentDTO.getFirstName() == null) nullValue = true;
        if (parentDTO.getLastName() == null) nullValue = true;
        if (parentDTO.getDateOfBirth() == null) nullValue = true;
        if (parentDTO.getGender() == null) nullValue = true;
        if (parentDTO.getOccupation() == null) nullValue = true;

        if (nullValue) {
            throw new BadRequestException("Incomplete data provided.");
        }

        parent.setEmail(parentDTO.getEmail());
        parent.setPhone(parentDTO.getPhone());
        parent.setAddress(parentDTO.getAddress());
        parent.setFirstName(parentDTO.getFirstName());
        parent.setLastName(parentDTO.getLastName());
        parent.setDateOfBirth(parentDTO.getDateOfBirth());
        parent.setGender(parentDTO.getGender());
        parent.setOccupation(parentDTO.getOccupation());

        return parent;
    }

}