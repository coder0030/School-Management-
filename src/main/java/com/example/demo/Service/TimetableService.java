package com.example.demo.Service;

import com.example.demo.DTO.TimetableDTO;
import com.example.demo.RequestDTO.TimetableRequestDTO;
import jakarta.validation.Valid;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface TimetableService {

    TimetableDTO createTimetable(TimetableRequestDTO requestDTO);

    TimetableDTO getTimetableById(Long id);

    List<TimetableDTO> getAllTimetables();

    TimetableDTO updateTimetable(Long id, TimetableRequestDTO requestDTO);

    void deleteTimetable(Long id);

    List<TimetableDTO> getTimetableByClass(Long classId);

    List<TimetableDTO> getTimetableByTeacher(Long teacherId);

    List<TimetableDTO> getTimetableByClassAndDay(Long classId, DayOfWeek dayOfWeek);

    List<TimetableDTO> getTimetableByTeacherAndDay(Long teacherId, DayOfWeek dayOfWeek);

    boolean isTimeSlotAvailable(Long classId, Long teacherId, DayOfWeek dayOfWeek,
                                Integer periodNumber, LocalTime startTime, LocalTime endTime,
                                Long excludeId);

    TimetableDTO partialUpdateTimetable(Long id, @Valid TimetableRequestDTO requestDTO);
}