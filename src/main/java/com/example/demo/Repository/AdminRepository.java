package com.example.demo.Repository;

import com.example.demo.ENTITY.Admin;
import com.example.demo.Helper.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUserId(Long userId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long excludeStudentId);

    boolean existsByPhoneAndIdNot(String phone, Long excludeStudentId);

    Optional<Admin> findByEmail(String email);

    List<Admin> findByUser_RolesAndIsActive(Role role, Boolean isActive);

    List<Admin> findByIsActive(boolean b);

    Optional<Admin> findByUser_Id(Long currUserId);
}