package com.example.demo.Controller;

import com.example.demo.DTO.LibrarianDTO;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.LibrarianRequestDTO;
import com.example.demo.Service.LibrarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarians")
@RequiredArgsConstructor
public class LibrarianController {

    private final LibrarianService librarianService;

    @PreAuthorize("hasRole('ADMIN','SUPERADMIN')")
    @PostMapping("/create")
    public ResponseEntity<LibrarianDTO> createLibrarian(@Valid @RequestBody LibrarianRequestDTO requestDTO) {
        LibrarianDTO response = librarianService.createLibrarian(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or (hasRole('ADMIN') or @securityUtil.isCurrLibrarian(#id))")
    public ResponseEntity<LibrarianDTO> getLibrarianById(@PathVariable Long id) {
        LibrarianDTO response = librarianService.getLibrarianById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{librarianCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibrarianDTO> getLibrarianByCode(@PathVariable String librarianCode) {
        LibrarianDTO response = librarianService.getLibrarianByCode(librarianCode);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<LibrarianDTO> getLibrarianByEmail(@PathVariable String email) {
        LibrarianDTO response = librarianService.getLibrarianByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<LibrarianDTO>> getAllLibrarians(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<LibrarianDTO> responses = librarianService.getAllLibrarians(pageNo, pageSize);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<LibrarianDTO>> getLibrariansByRole(@PathVariable String role) {
        List<LibrarianDTO> responses = librarianService.getLibrariansByRole(role);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<LibrarianDTO>> getActiveLibrarians() {
        List<LibrarianDTO> responses = librarianService.getActiveLibrarians();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/updateId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityUtil.isCurrLibrarian(#id)")
    public ResponseEntity<LibrarianDTO> updateLibrarian(
            @PathVariable Long id,
            @Valid @RequestBody LibrarianRequestDTO requestDTO) {
        LibrarianDTO response = librarianService.updateLibrarian(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("partialUpdateId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityUtil.isCurrLibrarian(#id)")
    public ResponseEntity<LibrarianDTO> partialUpdateLibrarian(
            @PathVariable Long id,
            @Valid @RequestBody LibrarianRequestDTO requestDTO) {
        LibrarianDTO response = librarianService.partialUpdateLibrarian(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteId/{id}")
    @PreAuthorize("""
            hasRole('SUPERADMIN') and @securityUtil.isCurrLibrarian(#id)
      """)
    public ResponseEntity<Void> deleteLibrarian(@PathVariable Long id) {
        librarianService.deleteLibrarian(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<LibrarianDTO> activateLibrarian(@PathVariable Long id) {
        LibrarianDTO response = librarianService.activateLibrarian(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("""
            hasRole('SUPERADMIN') and @securityUtil.isCurrLibrarian(#id)
      """)
    public ResponseEntity<LibrarianDTO> deactivateLibrarian(@PathVariable Long id) {
        LibrarianDTO response = librarianService.deactivateLibrarian(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{librarianId}/addRole")
    @PreAuthorize("""
            hasRole('SUPERADMIN') and @securityUtil.isCurrLibrarian(#librarianId)
            and #role.name() != 'SUPERADMIN'
      """)
    public ResponseEntity<LibrarianDTO> addRoleToLibrarian(@PathVariable Long librarianId, @RequestBody Role role) {
        LibrarianDTO librarianDTO = librarianService.addRoleToLibrarian(librarianId, role);
        return ResponseEntity.ok(librarianDTO);
    }

    @PostMapping("/{librarianId}/removeRole")
    @PreAuthorize("""
         hasRole('SUPERADMIN') and @securityUtil.isCurrLibrarian(#librarianId)
         """)
    public ResponseEntity<LibrarianDTO> removeRoleFromLibrarian(@PathVariable Long librarianId, @RequestBody Role role) {
        LibrarianDTO librarianDTO = librarianService.removeRoleFromLibrarian(librarianId, role);
        return ResponseEntity.ok(librarianDTO);
    }
}