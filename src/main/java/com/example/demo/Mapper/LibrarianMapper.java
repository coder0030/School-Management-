package com.example.demo.Mapper;

import com.example.demo.DTO.LibrarianDTO;
import com.example.demo.ENTITY.Librarian;
import com.example.demo.Helper.Role;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.RequestDTO.LibrarianRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LibrarianMapper {

    public LibrarianDTO toDto(Librarian librarian) {
        if (librarian == null) return null;

        return LibrarianDTO.builder()
                .id(librarian.getId())
                .firstName(librarian.getFirstName())
                .lastName(librarian.getLastName())
                .email(librarian.getEmail())
                .phone(librarian.getPhone())
                .address(librarian.getAddress())
                .roles(librarian.getUser().getRoles())
                .isActive(librarian.getIsActive())
                .build();
    }

    public Librarian toEntity(LibrarianRequestDTO librarianDTO, Librarian librarian) {
        if (librarianDTO == null) return null;

        if (librarianDTO.getFirstName() != null) {
            librarian.setFirstName(librarianDTO.getFirstName());
        }
        if (librarianDTO.getLastName() != null) {
            librarian.setLastName(librarianDTO.getLastName());
        }
        if (librarianDTO.getEmail() != null) {
            librarian.setEmail(librarianDTO.getEmail());
        }
        if (librarianDTO.getPhone() != null) {
            librarian.setPhone(librarianDTO.getPhone());
        }
        if (librarianDTO.getAddress() != null) {
            librarian.setAddress(librarianDTO.getAddress());
        }
        if (librarianDTO.getRole() != null) {
            librarian.addRoles(Role.valueOf(librarianDTO.getRole()));
        }
        if (librarianDTO.getIsActive() != null) {
            librarian.setIsActive(librarianDTO.getIsActive());
        }

        return librarian;
    }

    public List<LibrarianDTO> toDtoList(List<Librarian> librarians) {
        if (librarians == null || librarians.isEmpty()) return null;
        return librarians.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Librarian updateToEntity(LibrarianRequestDTO librarianDTO, Librarian librarian) {
        boolean nullValue = false;

        if (librarianDTO.getFirstName() == null) nullValue = true;
        if (librarianDTO.getLastName() == null) nullValue = true;
        if (librarianDTO.getEmail() == null) nullValue = true;
        if (librarianDTO.getPhone() == null) nullValue = true;
        if (librarianDTO.getAddress() == null) nullValue = true;
        if (librarianDTO.getRole() == null) nullValue = true;
        if (librarianDTO.getIsActive() == null) nullValue = true;

        if (nullValue) {
            throw new BadRequestException("Incomplete data provided.");
        }

        librarian.setFirstName(librarianDTO.getFirstName());
        librarian.setLastName(librarianDTO.getLastName());
        librarian.setEmail(librarianDTO.getEmail());
        librarian.setPhone(librarianDTO.getPhone());
        librarian.setAddress(librarianDTO.getAddress());
        librarian.addRoles(Role.valueOf(librarianDTO.getRole()));
        librarian.setIsActive(librarianDTO.getIsActive());

        return librarian;
    }
}