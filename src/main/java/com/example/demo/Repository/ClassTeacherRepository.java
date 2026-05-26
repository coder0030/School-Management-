package com.example.demo.Repository;

import com.example.demo.ENTITY.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Long> {

    Optional<ClassTeacher> findByClassEntityIdAndTeacherId(Long classId, Long teacherId);

    List<ClassTeacher> findAllByClassEntity_Id(Long classId);

    boolean existsByTeacher_IdAndClassEntity_Id(Long teacherId, Long classId);

    boolean existsByClassEntity_IdAndIsPrimary(Long classId, boolean b);

    Optional<ClassTeacher> findByClassEntity_IdAndTeacher_Id(Long classId, Long teacherId);

    boolean existsByTeacher_IdAndIsPrimary(Long id, boolean b);
}