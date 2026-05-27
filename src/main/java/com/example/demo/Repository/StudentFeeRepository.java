package com.example.demo.Repository;

import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.StudentFee;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {

    List<StudentFee> findByClassEntity_Id(Long classId);

    List<StudentFee> findByStudent_Id(Long studentId);

    boolean existsByStudent_IdAndClassEntity_IdAndFeeStructure_Id(Long id, Long id1, Long id2);
}