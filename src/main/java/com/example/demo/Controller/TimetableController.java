package com.example.demo.Controller;

import com.example.demo.DTO.TimetableDTO;
import com.example.demo.RequestDTO.TimetableRequestDTO;
import com.example.demo.Service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<TimetableDTO> createTimetable(@Valid @RequestBody TimetableRequestDTO requestDTO) {
        TimetableDTO createdTimetable = timetableService.createTimetable(requestDTO);
        return new ResponseEntity<>(createdTimetable, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TimetableDTO> getTimetableById(@PathVariable Long id) {
        TimetableDTO timetable = timetableService.getTimetableById(id);
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<TimetableDTO>> getAllTimetables() {
        List<TimetableDTO> timetables = timetableService.getAllTimetables();
        return ResponseEntity.ok(timetables);
    }

    @PutMapping("updateId/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<TimetableDTO> updateTimetable(
            @PathVariable Long id,
            @Valid @RequestBody TimetableRequestDTO requestDTO) {
        TimetableDTO updatedTimetable = timetableService.updateTimetable(id, requestDTO);
        return ResponseEntity.ok(updatedTimetable);
    }

    @DeleteMapping("deleteId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteTimetable(@PathVariable Long id) {
        timetableService.deleteTimetable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<TimetableDTO>> getTimetableByClass(@PathVariable Long classId) {
        List<TimetableDTO> timetables = timetableService.getTimetableByClass(classId);
        return ResponseEntity.ok(timetables);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER')")
    public ResponseEntity<List<TimetableDTO>> getTimetableByTeacher(@PathVariable Long teacherId) {
        List<TimetableDTO> timetables = timetableService.getTimetableByTeacher(teacherId);
        return ResponseEntity.ok(timetables);
    }

    @GetMapping("/class/{classId}/day/{dayOfWeek}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<TimetableDTO>> getTimetableByClassAndDay(
            @PathVariable Long classId,
            @PathVariable DayOfWeek dayOfWeek) {
        List<TimetableDTO> timetables = timetableService.getTimetableByClassAndDay(classId, dayOfWeek);
        return ResponseEntity.ok(timetables);
    }

    @GetMapping("/teacher/{teacherId}/day/{dayOfWeek}")
    @PreAuthorize("@securityUtil.isAdminOrOwner(#teacherId)")
    public ResponseEntity<List<TimetableDTO>> getTimetableByTeacherAndDay(
            @PathVariable Long teacherId,
            @PathVariable DayOfWeek dayOfWeek) {
        List<TimetableDTO> timetables = timetableService.getTimetableByTeacherAndDay(teacherId, dayOfWeek);
        return ResponseEntity.ok(timetables);
    }

    @GetMapping("/check-availability")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @RequestParam Long classId,
            @RequestParam Long teacherId,
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam Integer periodNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isAvailable = timetableService.isTimeSlotAvailable(
                classId, teacherId, dayOfWeek, periodNumber, null, null, excludeId);
        return ResponseEntity.ok(isAvailable);
    }

    @PatchMapping("/patchUpdate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<TimetableDTO> partialUpdateTimetable(@PathVariable Long id,
                                               @Valid @RequestBody TimetableRequestDTO requestDTO) {
        TimetableDTO updatedTimetable = timetableService.partialUpdateTimetable(id, requestDTO);
        return ResponseEntity.ok(updatedTimetable);
    }
}