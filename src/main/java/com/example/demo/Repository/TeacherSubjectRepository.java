package com.example.demo.Repository;

import com.example.demo.ENTITY.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {
    boolean existsByTeacher_IdAndSubject_IdAndClassEntity_Id(Long teacherId, Long subjectId, Long classId);

     Optional<TeacherSubject> findByTeacher_IdAndSubject_IdAndClassEntity_Id(Long teacherId, Long subjectId, Long classId);
}
