package com.example.demo.Repository;

import com.example.demo.ENTITY.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsBySubjectCode(String subjectCode);
    boolean existsBySubjectName(String subjectName);

    List<Subject> findAllBySubjectCodeOrSubjectName(String subjectCode, String subjectName);
}
