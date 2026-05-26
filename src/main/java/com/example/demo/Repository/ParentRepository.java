package com.example.demo.Repository;

import com.example.demo.ENTITY.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, Long excludeStudentId);

    boolean existsByPhoneAndIdNot(String phone, Long excludeStudentId);

    Optional<Parent> findByEmail(String email);

    @Query("SELECT p FROM Parent p JOIN p.studentList s GROUP BY p HAVING COUNT(s) > 1")
    Page<Parent> findParentsWithMultipleChildren(Pageable pageable);

    Optional<Parent> findByUser_Id(Long currUserId);
}
