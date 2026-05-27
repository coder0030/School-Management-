package com.example.demo.Repository;

import com.example.demo.ENTITY.Student;
import com.example.demo.Helper.Status;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByPhone(@Param("phone") String phone);

    boolean existsByEmail(@NotBlank String email);

    Optional<Student> findById(Long id);

    @Query("SELECT MAX(s.rollNo) FROM Student s WHERE s.classEntity.id = :id")
    Optional<Integer> findMaxRollNoByClassId(Long id);

    boolean existsByEmailOrPhone(String email, String phone);

    Optional<Student> findByEmail(String email);

    List<Student> findByClassEntityIsNotNull();

    Student findByClassEntity_IdAndRollNo(Long classId, int rollNo);

    Student findByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, Long excludeStudentId);

    boolean existsByPhoneAndIdNot(String phone, Long excludeStudentId);

    boolean existsByIdAndClassEntity_Id(Long id, Long id1);

    int countByClassEntity_Id(Long id);

    List<Student> findByClassEntity_Id(Long id);

    Optional<Student> findByIdAndStatus(Long id, Status status);

    Optional<Student> findByUser_Id(Long currUserId);
}
