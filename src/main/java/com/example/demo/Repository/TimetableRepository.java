package com.example.demo.Repository;

import com.example.demo.ENTITY.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByTeacher_Id(Long teacherId);

    List<Timetable> findByClassEntity_Id(Long classId);
    
    List<Timetable> findByClassEntity_IdAndDayOfWeek(Long classId, DayOfWeek dayOfWeek);

    List<Timetable> findByTeacher_IdAndDayOfWeek(Long teacherId, DayOfWeek dayOfWeek);

    boolean existsByTeacher_IdAndDayOfWeek(Long id, DayOfWeek dayOfWeek);

    boolean existsByClassEntity_IdAndDayOfWeekAndPeriodNumberAndIdNot(Long classId, DayOfWeek dayOfWeek, Integer periodNumber, Long exclude);

    List<Timetable> findByTeacher_IdAndDayOfWeekAndIdNot(Long teacherId, DayOfWeek dayOfWeek, Long exclude);

    boolean existsByTeacher_IdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long teacherId, DayOfWeek currentDay, LocalTime currentTime, LocalTime currentTime1);

    Timetable findByTeacher_IdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long teacherId, DayOfWeek currentDay, LocalTime currentTime, LocalTime currentTime1);
}
