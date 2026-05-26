package com.example.demo.Controller;

import com.example.demo.DTO.ExamDTO;
import com.example.demo.RequestDTO.ExamRequestDTO;
import com.example.demo.Service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<ExamDTO> createExam(@Valid @RequestBody ExamRequestDTO dto) {
        return new ResponseEntity<>(examService.createExam(dto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<ExamDTO>> getAllExams(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ExamDTO> exams = examService.getAllExams(pageNo, pageSize);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<ExamDTO> getExamById(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }

    @PutMapping("/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<ExamDTO> updateExam(
            @PathVariable Long examId,
            @Valid @RequestBody ExamRequestDTO dto) {
        return ResponseEntity.ok(examService.updateExam(examId, dto));
    }

    @PatchMapping("/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<ExamDTO> partialUpdateExam(
            @PathVariable Long examId,
            @RequestBody ExamRequestDTO dto) {
        return ResponseEntity.ok(examService.partialUpdateExam(examId, dto));
    }

    @DeleteMapping("/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId) {
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN', 'TEACHER')")
    public ResponseEntity<Page<ExamDTO>> getExamsByClass(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ExamDTO> exams = examService.getExamsByClassId(classId, pageNo, pageSize);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<ExamDTO>> getUpcomingExams(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ExamDTO> exams = examService.getUpcomingExams(pageNo, pageSize);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/past")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<Page<ExamDTO>> getPastExams(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ExamDTO> exams = examService.getPastExams(pageNo, pageSize);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("""
           hasAnyRole('ADMIN','SUPERADMIN', 'TEACHER'
           or @securityUtil.isCurrStudent(#studentId)
           """)
    public ResponseEntity<Page<ExamDTO>> getExamsByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ExamDTO> exams = examService.getExamsByStudentId(studentId, pageNo, pageSize);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER')")
    public ResponseEntity<Page<ExamDTO>> getExamsBySubject(
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ExamDTO> exams = examService.getExamsBySubjectId(subjectId, pageNo, pageSize);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER')")
    public ResponseEntity<Page<ExamDTO>> getExamsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ExamDTO> exams = examService.getExamsByDateRange(startDate, endDate, pageNo, pageSize);
        return ResponseEntity.ok(exams);
    }
}