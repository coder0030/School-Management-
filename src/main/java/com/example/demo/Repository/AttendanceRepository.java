package com.example.demo.Repository;

import com.example.demo.DTO.AttendanceDTO;
import com.example.demo.ENTITY.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudent_IdAndAttendanceDate(
            Long studentId, LocalDate attendanceDate);

    List<Attendance> findAllByStudent_Id(Long studentId);

    List<Attendance> findByTimetable_IdAndAttendanceDate(
            Long timetableId, LocalDate attendanceDate);

    boolean existsByStudent_IdAndAttendanceDateAndTimetable_Id(
            Long studentId, LocalDate attendanceDate, Long timetableId);

//    @Query("SELECT a FROM Attendance a WHERE a.student.id = studentId")
//    List<Attendance> findAttendanceByStudentId(@Param("studentId") Long studentId);

    Page<Attendance> findAllByStudentId(Long studentId, Pageable pageable);

    Page<Attendance> findByStudent_ClassEntity_IdAndAttendanceDate(Long classId, LocalDate date, Pageable pageable);
}