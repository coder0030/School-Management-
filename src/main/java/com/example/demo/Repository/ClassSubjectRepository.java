package com.example.demo.Repository;

import com.example.demo.ENTITY.ClassSubject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassSubjectRepository extends JpaRepository<ClassSubject, Long> {

    boolean existsByClassEntityIdAndSubjectId(Long classId, Long subjectId);

    Optional<ClassSubject> findByClassEntityIdAndSubjectId(Long classId, Long subjectId);

    Page<ClassSubject> findByClassEntityId(Long classId, Pageable pageable);

    List<ClassSubject> findBySubjectId(Long subjectId);

    boolean existsBySubject_IdAndClassEntity_Id(Long subjectId, Long classId);

    boolean existsByClassEntity_IdAndSubject_IdAndTeacher_IdAndIdNot(Long classId, Long subjectId, Long teacherId, Long id);

    boolean existsByClassEntity_IdAndSubject_IdAndTeacher_Id(Long classId, Long subjectId, Long teacherId);

    Optional<ClassSubject> findByClassEntity_IdAndSubject_IdAndTeacher_Id(Long classId, Long subjectId, Long teacherId);
}
