package com.example.demo.ServiceImpl;

import com.example.demo.DTO.AdminDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.AdminMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.AdminRequestDTO;
import com.example.demo.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminImplService implements AdminService {

    private final AdminRepository adminRepository;
    private final AllRepositoryMethods allRepositoryMethods;
    private final AdminMapper adminMapper;
    private final UserRepository userRepository;

    private void checkExistenceForCreate(String email, String phone) {
        if (email != null && adminRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && adminRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private void checkExistenceForUpdate(String email, String phone, Long excludeStudentId) {
        if (email != null && adminRepository.existsByEmailAndIdNot(email, excludeStudentId)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && adminRepository.existsByPhoneAndIdNot(phone, excludeStudentId)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }


    @Override
    public AdminDTO createAdmin(AdminRequestDTO requestDTO) {
        checkExistenceForCreate(requestDTO.getEmail(), requestDTO.getPhone());
        User user = userRepository.findByEmailOrUsernameAndIsActive(requestDTO.getEmail(), requestDTO.getEmail(), true)
                .orElseThrow(() -> new BadRequestException
                        ("No active user account found with this email. User must signup first."));

        Admin admin = new Admin();
        admin.addRoles(Role.ROLE_ADMIN);
        admin = adminMapper.toEntity(requestDTO, admin);
        admin.setUser(user);

        return adminMapper.toDto(adminRepository.save(admin));
    }

    @Override
    public AdminDTO getAdminById(Long id) {
        Admin admin = allRepositoryMethods.getAdminById(id);
        return adminMapper.toDto(admin);
    }

    @Override
    public AdminDTO getAdminByCode(String adminCode) {
        return null;
    }

    @Override
    public AdminDTO getAdminByEmail(String email) {
        Admin admin = allRepositoryMethods.getAdminByEmail(email);
        return adminMapper.toDto(admin);
    }

    @Override
    public Page<AdminDTO> getAllAdmins(int pageNo, int pageSize) {
        if (pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


        Page<Admin> adminPage = adminRepository.findAll(pageable);

        if (adminPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return adminPage.map(adminMapper::toDto);
    }

    @Override
    public List<AdminDTO> getAdminsByRole(String role) {
        Role roles = Role.getRoles(role);
        List<Admin> adminList = adminRepository.findByUser_RolesAndIsActive(roles, true);
        if(adminList.isEmpty()) {
            return List.of();
        }

        return adminMapper.toDtoList(adminList);
    }

    @Override
    public List<AdminDTO> getActiveAdmins() {
        List<Admin> adminList = adminRepository.findByIsActive(true);
        if(adminList.isEmpty()) {
            return List.of();
        }

        return adminMapper.toDtoList(adminList);
    }

    @Override
    public AdminDTO updateAdmin(Long id, AdminRequestDTO requestDTO) {
        Admin admin = allRepositoryMethods.getAdminByEmail(requestDTO.getEmail());
        String email = (requestDTO.getEmail() != null ? requestDTO.getEmail() : admin.getEmail());
        String phone = (requestDTO.getPhone() != null ? requestDTO.getPhone() : admin.getPhone());

        admin.setEmail(email);
        admin.setPhone(phone);

        checkExistenceForUpdate(email, phone, id);
        admin = adminMapper.updateToEntity(requestDTO, admin);
        return adminMapper.toDto(adminRepository.save(admin));
    }

    @Override
    public AdminDTO partialUpdateAdmin(Long id, AdminRequestDTO requestDTO) {
        Admin admin = allRepositoryMethods.getAdminByEmail(requestDTO.getEmail());
        String email = (requestDTO.getEmail() != null ? requestDTO.getEmail() : admin.getEmail());
        String phone = (requestDTO.getPhone() != null ? requestDTO.getPhone() : admin.getPhone());

        checkExistenceForUpdate(email, phone, id);

        admin.setEmail(email);
        admin.setPhone(phone);

        admin = adminMapper.toEntity(requestDTO, admin);
        return adminMapper.toDto(adminRepository.save(admin));
    }

    @Override
    public void deleteAdmin(Long id) {
        Admin admin = allRepositoryMethods.getAdminById(id);
        User user = admin.getUser();
        user.setDeleted(true);
        adminRepository.delete(admin);
    }

    @Override
    public AdminDTO activateAdmin(Long id) {
        Admin admin = allRepositoryMethods.getAdminById(id);
        admin.setIsActive(true);
        return adminMapper.toDto(adminRepository.save(admin));
    }

    @Override
    public AdminDTO deactivateAdmin(Long id) {
        Admin admin = allRepositoryMethods.getAdminById(id);
        admin.setIsActive(false);
        return adminMapper.toDto(adminRepository.save(admin));
    }

    @Override
    public AdminDTO addRoleToAdmin(Long adminId, Role role) {
        Admin admin = allRepositoryMethods.getAdminById(adminId);
        if(admin.getUser().getRoles().contains(role)) {
            throw new BadRequestException("Admin already assigned with role : " + role);
        }

        admin.addRoles(role);
        return adminMapper.toDto(adminRepository.save(admin));
    }

    @Override
    public AdminDTO removeRoleToAdmin(Long adminId, Role role) {
        Admin admin = allRepositoryMethods.getAdminById(adminId);
        if(!admin.getUser().getRoles().contains(role)) {
            throw new BadRequestException("Admin has not assigned with role : " + role);
        }

        admin.removeRoles(role);
        return adminMapper.toDto(adminRepository.save(admin));
    }
}
