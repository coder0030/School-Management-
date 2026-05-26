package com.example.demo.Controller;

import com.example.demo.DTO.AttendanceBulkResponseDTO;
import com.example.demo.DTO.AttendanceDTO;
import com.example.demo.Helper.AttendanceStatus;
import com.example.demo.RequestDTO.AttendanceBulkRequestDTO;
import com.example.demo.RequestDTO.AttendanceRequestDTO;
import com.example.demo.Service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/create/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER')")
    public ResponseEntity<AttendanceDTO> markAttendance(
            @PathVariable Long studentId,
            @Valid @RequestBody AttendanceRequestDTO attendanceRequestDTO) {

        AttendanceDTO createdAttendance = attendanceService.markAttendance(studentId, attendanceRequestDTO);
        return new ResponseEntity<>(createdAttendance, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<Page<AttendanceDTO>> getAllAttendanceRecords(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<AttendanceDTO> attendanceList = attendanceService.getAllAttendanceRecords(pageNo, pageSize);
        return new ResponseEntity<>(attendanceList, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/date/{date}")
    @PreAuthorize("""
            hasAnyRole('ADMIN','SUPERADMIN','TEACHER','CLASSTEACHER')
            or @securityUtil.isCurrStudent(#studentId)
    """)
    public ResponseEntity<AttendanceDTO> getStudentAttendanceByDate(
            @PathVariable Long studentId,
            @PathVariable LocalDate date) {
        AttendanceDTO attendanceDTO = attendanceService.getStudentAttendanceByDate(studentId, date);
        return new ResponseEntity<>(attendanceDTO, HttpStatus.OK);
    }

    @DeleteMapping("/student/{studentId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<String> deleteStudentAttendanceByDate(
            @PathVariable Long studentId,
            @PathVariable LocalDate date) {
        attendanceService.deleteStudentAttendanceByDate(studentId, date);
        return ResponseEntity.ok("Attendance deleted successfully");
    }

    @DeleteMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<String> deleteAllAttendanceOfStudent(@PathVariable Long studentId) {
        attendanceService.deleteAllAttendanceOfStudent(studentId);
        return ResponseEntity.ok("All attendance records deleted successfully");
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("""
            hasAnyRole('ADMIN','SUPERADMIN','TEACHER','CLASSTEACHER')
            or @securityUtil.isCurrStudent(#studentId)
    """)
    public ResponseEntity<Page<AttendanceDTO>> getAllAttendanceOfStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<AttendanceDTO> attendanceDTO = attendanceService.getAllAttendanceOfStudent(studentId, pageNo, pageSize);
        return new ResponseEntity<>(attendanceDTO, HttpStatus.OK);
    }

    @PutMapping("/update/{studentId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER')")
    public ResponseEntity<AttendanceDTO> updateStudentAttendance(
            @PathVariable Long studentId,
            @PathVariable LocalDate date,
            @Valid @RequestBody AttendanceRequestDTO attendanceRequestDTO) {

        AttendanceDTO updatedAttendance = attendanceService.updateStudentAttendance(studentId, date, attendanceRequestDTO);
        return new ResponseEntity<>(updatedAttendance, HttpStatus.OK);
    }

    @GetMapping("/class/{classId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','CLASSTEACHER')")
    public ResponseEntity<Page<AttendanceDTO>> getAttendanceByClassAndDate(
            @PathVariable Long classId,
            @PathVariable LocalDate date,
            int pageNo, int pageSize) {
        Page<AttendanceDTO> attendanceList = attendanceService.getAttendanceByClassAndDate(classId, date, pageNo, pageSize);
        return new ResponseEntity<>(attendanceList, HttpStatus.OK);
    }

    @GetMapping("/timetable/{timetableId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER')")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByTimetableAndDate(
            @PathVariable Long timetableId,
            @PathVariable LocalDate date) {
        List<AttendanceDTO> attendanceList = attendanceService.getAttendanceByTimetableAndDate(timetableId, date);
        return new ResponseEntity<>(attendanceList, HttpStatus.OK);
    }

    @PostMapping("/bulk-mark")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER')")
    public ResponseEntity<AttendanceBulkResponseDTO> AttendanceBulkResponseDTO(@RequestBody AttendanceBulkRequestDTO requestDTO) {
        AttendanceBulkResponseDTO responseDTO = attendanceService.markBulkAttendance(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}