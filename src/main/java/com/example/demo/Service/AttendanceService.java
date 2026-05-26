package com.example.demo.Service;

import com.example.demo.DTO.AttendanceBulkResponseDTO;
import com.example.demo.DTO.AttendanceDTO;
import com.example.demo.RequestDTO.AttendanceBulkRequestDTO;
import com.example.demo.RequestDTO.AttendanceRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceDTO markAttendance(Long studentId, AttendanceRequestDTO requestDTO);

    AttendanceDTO getStudentAttendanceByDate(Long studentId, LocalDate date);

    void deleteStudentAttendanceByDate(Long studentId, LocalDate date);

    void deleteAllAttendanceOfStudent(Long studentId);

    Page<AttendanceDTO> getAllAttendanceRecords(int pageNo, int pageSize);

    Page<AttendanceDTO> getAllAttendanceOfStudent(Long studentId, int pageNo, int pageSize);

    AttendanceDTO updateStudentAttendance(Long studentId, LocalDate date, AttendanceRequestDTO requestDTO);

    Page<AttendanceDTO> getAttendanceByClassAndDate(Long classId, LocalDate date, int pageNo, int pageSize);

    List<AttendanceDTO> getAttendanceByTimetableAndDate(Long timetableId, LocalDate date);

    AttendanceBulkResponseDTO markBulkAttendance(AttendanceBulkRequestDTO requestDTO);
}