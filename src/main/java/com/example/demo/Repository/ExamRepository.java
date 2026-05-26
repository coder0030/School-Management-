package com.example.demo.Repository;

import com.example.demo.ENTITY.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    boolean existsByExamNameAndExamTypeAndAcademicYearAndClassEntity_Id(String examName, String examType, String currYear, Long classId);

    Page<Exam> findByClassEntity_Id(Long classId, Pageable pageable);

    Page<Exam> findByStartDateGreaterThanEqual(LocalDate today, Pageable pageable);

    Page<Exam> findByEndDateBefore(LocalDate today, Pageable pageable);
}
