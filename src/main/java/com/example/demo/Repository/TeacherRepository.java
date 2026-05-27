package com.example.demo.Repository;

import com.example.demo.ENTITY.Department;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Role;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByEmail(@NotBlank String email);

    boolean existsByEmailOrPhone(String email, String phone);

    boolean existsByIdAndClassTeacherList_ClassEntity_IdAndClassTeacherList_ClassEntity_ClassSubjects_Subject_Id(Long teacherId, Long classId, Long subjectId);

    boolean existsByPhone(String phone);

    boolean existsByEmailOrPhoneAndIdNot(String email, String phone, Long teacherId);

    Optional<Teacher> findByIdAndTeacherSubjects_Subject_Id(Long teacherId, Long subjectId);

    Optional<Teacher> existsByIdAndTeacherSubjects_Subject_Id(Long id, Long id1);

    boolean existsByEmailAndIdNot(String email, Long teacherId);

    boolean existsByPhoneAndIdNot(String phone, Long teacherId);

    boolean existsByIdAndRole(Long id, Role role);

    List<Teacher> findByUser(User user);

    Optional<Teacher> findByUser_Id(Long currUserId);
}
