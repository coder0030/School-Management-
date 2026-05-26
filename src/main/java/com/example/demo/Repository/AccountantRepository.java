package com.example.demo.Repository;

import com.example.demo.ENTITY.Accountant;
import com.example.demo.Helper.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountantRepository extends JpaRepository<Accountant, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, Long excludeAccountantId);

    boolean existsByPhoneAndIdNot(String phone, Long excludeAccountantId);

    Optional<Accountant> findByEmail(String email);

    List<Accountant> findByUser_RolesAndIsActive(Role roles, boolean b);

    Optional<Accountant> findByUser_Id(Long currUserId);
}
