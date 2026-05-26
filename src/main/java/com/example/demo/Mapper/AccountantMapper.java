package com.example.demo.Mapper;

import com.example.demo.DTO.AccountantDTO;
import com.example.demo.ENTITY.Accountant;
import com.example.demo.Helper.Role;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.RequestDTO.AccountantRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountantMapper {

    public AccountantDTO toDto(Accountant accountant) {
        if (accountant == null) return null;

        return AccountantDTO.builder()
                .id(accountant.getId())
                .firstName(accountant.getFirstName())
                .lastName(accountant.getLastName())
                .email(accountant.getEmail())
                .phone(accountant.getPhone())
                .address(accountant.getAddress())
                .roles(accountant.getUser().getRoles())
                .isActive(accountant.getIsActive())
                .build();
    }

    public Accountant toEntity(AccountantRequestDTO accountantDTO, Accountant accountant) {
        if (accountantDTO == null) return null;

        if (accountantDTO.getFirstName() != null) {
            accountant.setFirstName(accountantDTO.getFirstName());
        }
        if (accountantDTO.getLastName() != null) {
            accountant.setLastName(accountantDTO.getLastName());
        }
        if (accountantDTO.getEmail() != null) {
            accountant.setEmail(accountantDTO.getEmail());
        }
        if (accountantDTO.getPhone() != null) {
            accountant.setPhone(accountantDTO.getPhone());
        }
        if (accountantDTO.getAddress() != null) {
            accountant.setAddress(accountantDTO.getAddress());
        }
        if (accountantDTO.getRole() != null) {
            accountant.addRoles(Role.valueOf(accountantDTO.getRole()));
        }
        if (accountantDTO.getIsActive() != null) {
            accountant.setIsActive(accountantDTO.getIsActive());
        }

        return accountant;
    }

    public List<AccountantDTO> toDtoList(List<Accountant> accountants) {
        if (accountants == null || accountants.isEmpty()) return null;
        return accountants.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Accountant updateToEntity(AccountantRequestDTO accountantDTO, Accountant accountant) {
        boolean nullValue = false;

        if (accountantDTO.getFirstName() == null) nullValue = true;
        if (accountantDTO.getLastName() == null) nullValue = true;
        if (accountantDTO.getEmail() == null) nullValue = true;
        if (accountantDTO.getPhone() == null) nullValue = true;
        if (accountantDTO.getAddress() == null) nullValue = true;
        if (accountantDTO.getRole() == null) nullValue = true;
        if (accountantDTO.getIsActive() == null) nullValue = true;

        if (nullValue) {
            throw new BadRequestException("Incomplete data provided.");
        }

        accountant.setFirstName(accountantDTO.getFirstName());
        accountant.setLastName(accountantDTO.getLastName());
        accountant.setEmail(accountantDTO.getEmail());
        accountant.setPhone(accountantDTO.getPhone());
        accountant.setAddress(accountantDTO.getAddress());
        accountant.addRoles(Role.valueOf(accountantDTO.getRole()));
        accountant.setIsActive(accountantDTO.getIsActive());

        return accountant;
    }
}