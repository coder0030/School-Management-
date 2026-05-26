package com.example.demo.Mapper;

import com.example.demo.DTO.AdminDTO;
import com.example.demo.ENTITY.Admin;
import com.example.demo.Helper.Role;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.RequestDTO.AdminRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminMapper {

    public AdminDTO toDto(Admin admin) {
        if (admin == null) return null;

        return AdminDTO.builder()
                .id(admin.getId())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .address(admin.getAddress())
                .roles(admin.getUser().getRoles())
                .department(admin.getDepartment())
                .isActive(admin.getIsActive())
                .build();
    }

    public Admin toEntity(AdminRequestDTO adminDTO, Admin admin) {
        if (adminDTO == null) return null;

        if (adminDTO.getFirstName() != null) {
            admin.setFirstName(adminDTO.getFirstName());
        }
        if (adminDTO.getLastName() != null) {
            admin.setLastName(adminDTO.getLastName());
        }
        if (adminDTO.getEmail() != null) {
            admin.setEmail(adminDTO.getEmail());
        }
        if (adminDTO.getPhone() != null) {
            admin.setPhone(adminDTO.getPhone());
        }
        if (adminDTO.getAddress() != null) {
            admin.setAddress(adminDTO.getAddress());
        }
        if (adminDTO.getRole() != null) {
            admin.addRoles(Role.valueOf(adminDTO.getRole()));
        }
        if (adminDTO.getDepartment() != null) {
            admin.setDepartment(adminDTO.getDepartment());
        }
        if (adminDTO.getIsActive() != null) {
            admin.setIsActive(adminDTO.getIsActive());
        }

        return admin;
    }

    public List<AdminDTO> toDtoList(List<Admin> admins) {
        if (admins == null || admins.isEmpty()) return null;
        return admins.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Admin updateToEntity(AdminRequestDTO adminDTO, Admin admin) {
        boolean nullValue = false;

        if (adminDTO.getFirstName() == null) nullValue = true;
        if (adminDTO.getLastName() == null) nullValue = true;
        if (adminDTO.getEmail() == null) nullValue = true;
        if (adminDTO.getPhone() == null) nullValue = true;
        if (adminDTO.getAddress() == null) nullValue = true;
        if (adminDTO.getRole() == null) nullValue = true;
        if (adminDTO.getDepartment() == null) nullValue = true;
        if (adminDTO.getIsActive() == null) nullValue = true;

        if (nullValue) {
            throw new BadRequestException("Incomplete data provided.");
        }

        admin.setFirstName(adminDTO.getFirstName());
        admin.setLastName(adminDTO.getLastName());
        admin.setEmail(adminDTO.getEmail());
        admin.setPhone(adminDTO.getPhone());
        admin.setAddress(adminDTO.getAddress());
        Role.valueOf(adminDTO.getRole());
        admin.setDepartment(adminDTO.getDepartment());
        admin.setIsActive(adminDTO.getIsActive());

        return admin;
    }
}