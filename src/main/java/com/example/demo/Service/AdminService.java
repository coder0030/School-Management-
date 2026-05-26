package com.example.demo.Service;

import com.example.demo.DTO.AdminDTO;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.AdminRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {

    AdminDTO createAdmin(AdminRequestDTO requestDTO);

    AdminDTO getAdminById(Long id);

    AdminDTO getAdminByCode(String adminCode);

    AdminDTO getAdminByEmail(String email);

    Page<AdminDTO> getAllAdmins(int pageNo, int pageSize);

    List<AdminDTO> getAdminsByRole(String role);

    List<AdminDTO> getActiveAdmins();

    AdminDTO updateAdmin(Long id, AdminRequestDTO requestDTO);

    AdminDTO partialUpdateAdmin(Long id, AdminRequestDTO requestDTO);

    void deleteAdmin(Long id);

    AdminDTO activateAdmin(Long id);

    AdminDTO deactivateAdmin(Long id);

    AdminDTO addRoleToAdmin(Long adminId, Role role);

    AdminDTO removeRoleToAdmin(Long adminId, Role role);
}