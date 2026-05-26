package com.example.demo.Mapper;

import com.example.demo.DTO.TimetableDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.Subject;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.ENTITY.Timetable;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.RequestDTO.TimetableRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimetableMapper {

//    public Timetable toEntity(TimetableRequestDTO requestDTO,
//                              ClassEntity classEntity,
//                              Subject subject,
//                              Teacher teacher) {
//        if (requestDTO == null) {
//            return null;
//        }
//
//        return Timetable.builder()
//                .classEntity(classEntity)
//                .subject(subject)
//                .teacher(teacher)
//                .dayOfWeek(requestDTO.getDayOfWeek())
//                .periodNumber(requestDTO.getPeriodNumber())
//                .startTime(requestDTO.getStartTime())
//                .endTime(requestDTO.getEndTime())
//                .build();
//    }

    public TimetableDTO toDTO(Timetable timetable) {
        if (timetable == null) {
            return null;
        }

        return TimetableDTO.builder()
                .id(timetable.getId())
                .classId(timetable.getClassEntity() != null ? timetable.getClassEntity().getId() : null)
                .className(timetable.getClassEntity() != null ? timetable.getClassEntity().getClassName() : null)
                .subjectId(timetable.getSubject() != null ? timetable.getSubject().getId() : null)
                .subjectName(timetable.getSubject() != null ? timetable.getSubject().getSubjectName() : null)
                .subjectCode(timetable.getSubject() != null ? timetable.getSubject().getSubjectCode() : null)
                .teacherId(timetable.getTeacher() != null ? timetable.getTeacher().getId() : null)
                .teacherName(timetable.getTeacher() != null ? timetable.getTeacher().getTeacherName() : null)
                .teacherEmail(timetable.getTeacher() != null ? timetable.getTeacher().getEmail() : null)
                .dayOfWeek(timetable.getDayOfWeek())
                .periodNumber(timetable.getPeriodNumber())
                .startTime(timetable.getStartTime())
                .endTime(timetable.getEndTime())
                .build();
    }

    public Timetable toEntity(Timetable timetable, TimetableRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new BadRequestException("Request data cannot be null");
        }

        if (requestDTO.getDayOfWeek() == null ||
                requestDTO.getPeriodNumber() == null ||
                requestDTO.getStartTime() == null ||
                requestDTO.getEndTime() == null) {
            throw new BadRequestException("Incomplete information: all fields are required");
        }

        timetable.setDayOfWeek(requestDTO.getDayOfWeek());
        timetable.setPeriodNumber(requestDTO.getPeriodNumber());
        timetable.setStartTime(requestDTO.getStartTime());
        timetable.setEndTime(requestDTO.getEndTime());
        return timetable;
    }

    public Timetable partialUpdateEntity(Timetable timetable, TimetableRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new BadRequestException("Request data cannot be null");
        }

        if (requestDTO.getDayOfWeek() != null) {
            timetable.setDayOfWeek(requestDTO.getDayOfWeek());
        } else {
            throw new BadRequestException("Day of week is missing");
        }

        if (requestDTO.getPeriodNumber() != null) {
            timetable.setPeriodNumber(requestDTO.getPeriodNumber());
        } else {
            throw new BadRequestException("Period number is missing");
        }

        if (requestDTO.getStartTime() != null) {
            timetable.setStartTime(requestDTO.getStartTime());
        } else {
            throw new BadRequestException("Start time is missing");
        }

        if (requestDTO.getEndTime() != null) {
            timetable.setEndTime(requestDTO.getEndTime());
        } else {
            throw new BadRequestException("End time is missing");
        }
        return timetable;
    }

    public List<TimetableDTO> toDTOList(List<Timetable> timetables) {
        if(timetables.isEmpty()) {
            return null;
        }

        return timetables.stream().map((this::toDTO)).toList();
    }

}
