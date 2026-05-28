package com.example.demo.Controller;

import com.example.demo.DTO.AdminDTO;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.AdminRequestDTO;
import com.example.demo.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<AdminDTO> createAdmin(@Valid @RequestBody AdminRequestDTO requestDTO) {
        AdminDTO response = adminService.createAdmin(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('SUPERADMIN') or (hasRole('ADMIN') and @securityUtil.isCurrAdmin(#id))")
    @GetMapping("/id/{id}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Long id) {
        AdminDTO response = adminService.getAdminById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{adminCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDTO> getAdminByCode(@PathVariable String adminCode) {
        AdminDTO response = adminService.getAdminByCode(adminCode);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<AdminDTO> getAdminByEmail(@PathVariable String email) {
        AdminDTO response = adminService.getAdminByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<AdminDTO>> getAllAdmins(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<AdminDTO> responses = adminService.getAllAdmins(pageNo, pageSize);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<AdminDTO>> getAdminsByRole(@PathVariable String role) {
        List<AdminDTO> responses = adminService.getAdminsByRole(role);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<AdminDTO>> getActiveAdmins() {
        List<AdminDTO> responses = adminService.getActiveAdmins();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/updateId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityUtil.isCurrAdmin(#id)")
    public ResponseEntity<AdminDTO> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminRequestDTO requestDTO) {
        AdminDTO response = adminService.updateAdmin(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("partialUpdateId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityUtil.isCurrAdmin(#id)")
    public ResponseEntity<AdminDTO> partialUpdateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminRequestDTO requestDTO) {
        AdminDTO response = adminService.partialUpdateAdmin(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') and not @securityUtil.isCurrAdmin(#id)")  // Remove 'not'
    public ResponseEntity<Void> deleteAdmin(@PathVariable("id") Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<AdminDTO> activateAdmin(@PathVariable Long id) {
        AdminDTO response = adminService.activateAdmin(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("""
            hasRole('SUPERADMIN') and not @securityUtil.isCurrAdmin(#id)
      """)
    public ResponseEntity<AdminDTO> deactivateAdmin(@PathVariable Long id) {
        AdminDTO response = adminService.deactivateAdmin(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/{adminId}/addRole")
    @PreAuthorize("hasRole('SUPERADMIN') and not @securityUtil.isCurrAdmin(#adminId) and #role.name() != 'SUPERADMIN'")
    public ResponseEntity<AdminDTO> addRoleToAdmin(@PathVariable Long adminId, @RequestBody Role role) {
        AdminDTO adminDTO = adminService.addRoleToAdmin(adminId, role);
        return ResponseEntity.ok(adminDTO);
    }

    @DeleteMapping("/admin/{adminId}/removeRole")
    @PreAuthorize("hasRole('SUPERADMIN') and not @securityUtil.isCurrAdmin(#adminId)")
    public ResponseEntity<AdminDTO> removeRoleFromAdmin(@PathVariable Long adminId, @RequestBody Role role) {
        AdminDTO adminDTO = adminService.removeRoleToAdmin(adminId, role);
        return ResponseEntity.ok(adminDTO);
    }
}