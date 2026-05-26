package com.example.demo.Repository;

import com.example.demo.ENTITY.Marks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    boolean existsByStudent_IdAndSubject_IdAndExam_Id(Long studentId, Long subjectId, Long examId);

    Optional<Marks> findByStudent_IdAndSubject_Id(Long studentId, Long subjectId);

    List<Marks> findTopBySubject_IdOrderByMarksObtainedDesc(Long subjectId, int limit);

    boolean existsByStudent_IdAndSubject_IdAndExam_IdAndIdNot(Long id, Long id1, Long id2, Long id3);

    Page<Marks> findBySubject_Id(Long subjectId, Pageable pageable);

    Page<Marks> findByStudent_IdAndExam_Id(Long studentId, Long examId, Pageable pageable);

    Page<Marks> findByExam_Id(Long examId, Pageable pageable);
}