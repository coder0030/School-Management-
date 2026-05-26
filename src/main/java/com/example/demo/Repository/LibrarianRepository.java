package com.example.demo.Repository;

import com.example.demo.ENTITY.Librarian;
import com.example.demo.Helper.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long excludeStudentId);

    boolean existsByEmailAndIdNot(String email, Long excludeStudentId);

    Optional<Librarian> findByEmail(String email);

    List<Librarian> findByUser_RolesAndIsActive(Role role, boolean active);

    Optional<Librarian> findByUser_Id(Long currUserId);
}
