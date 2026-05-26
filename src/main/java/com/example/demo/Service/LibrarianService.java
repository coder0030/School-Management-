package com.example.demo.Service;

import com.example.demo.DTO.LibrarianDTO;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.LibrarianRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibrarianService {
    LibrarianDTO createLibrarian(@Valid LibrarianRequestDTO requestDTO);

    LibrarianDTO getLibrarianById(Long id);

    LibrarianDTO getLibrarianByCode(String librarianCode);

    LibrarianDTO getLibrarianByEmail(String email);

    Page<LibrarianDTO> getAllLibrarians(int pageNo, int pageSize);

    List<LibrarianDTO> getLibrariansByRole(String role);

    List<LibrarianDTO> getActiveLibrarians();

    LibrarianDTO updateLibrarian(Long id, @Valid LibrarianRequestDTO requestDTO);

    LibrarianDTO partialUpdateLibrarian(Long id, @Valid LibrarianRequestDTO requestDTO);

    void deleteLibrarian(Long id);

    LibrarianDTO activateLibrarian(Long id);

    LibrarianDTO deactivateLibrarian(Long id);

    LibrarianDTO addRoleToLibrarian(Long librarianId, Role role);

    LibrarianDTO removeRoleFromLibrarian(Long librarianId, Role role);
}
