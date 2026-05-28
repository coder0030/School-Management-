package com.example.demo.Controller;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.RequestDTO.MarksRequestDTO;
import com.example.demo.Service.MarksService;
import com.example.demo.ServiceImpl.MarksServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marks")
public class MarksController {

    private final MarksService marksService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<MarksDTO> createMarks(@RequestBody MarksRequestDTO marksRequestDTO) {
        MarksDTO createdMarks = marksService.createMarks(marksRequestDTO);
        return new ResponseEntity<>(createdMarks, HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER') or @securityUtil.isCurrStudent(#studentId)")
    public ResponseEntity<MarksDTO> getMarksById(@PathVariable Long studentId) {
        MarksDTO marksDTO = marksService.getMarksById(studentId);
        return ResponseEntity.ok(marksDTO);
    }

    @PutMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<MarksDTO> updateMarks(@PathVariable Long studentId, @RequestBody MarksRequestDTO marksRequestDTO) {
        MarksDTO updatedMarks = marksService.updateMarks(studentId, marksRequestDTO);
        return ResponseEntity.ok(updatedMarks);
    }

    @PatchMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<MarksDTO> partialUpdateMarks(@PathVariable Long studentId, @RequestBody MarksRequestDTO marksRequestDTO) {
        MarksDTO updatedMarks = marksService.partialUpdateMarks(studentId, marksRequestDTO);
        return ResponseEntity.ok(updatedMarks);
    }

    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<String> deleteMarks(@PathVariable Long studentId) {
        marksService.deleteMarks(studentId);
        return ResponseEntity.ok("Deleted successfully.");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Page<MarksDTO>> getAllMarks(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<MarksDTO> allMarks = marksService.getAllMarks(pageNo, pageSize);
        return ResponseEntity.ok(allMarks);
    }


    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<Page<MarksDTO>> getMarksBySubject(
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<MarksDTO> marksBySubject = marksService.getMarksBySubject(subjectId, pageNo, pageSize);
        return ResponseEntity.ok(marksBySubject);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER') or @securityUtil.isCurrStudent(#studentId)")
    public ResponseEntity<MarksDTO> getMarksByStudentAndSubject(@PathVariable Long studentId, @PathVariable Long subjectId) {
        MarksDTO marks = marksService.getMarksByStudentAndSubject(studentId, subjectId);
        return ResponseEntity.ok(marks);
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<List<MarksDTO>> createMarksInBulk(@RequestBody List<MarksRequestDTO> marksList) {
        List<MarksDTO> createdMarksList = marksService.createMarksInBulk(marksList);
        return new ResponseEntity<>(createdMarksList, HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}/exam/{examId}")
    @PreAuthorize("""
               hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')
               or #studentId == principal.id
               """)
    public ResponseEntity<Page<MarksDTO>> getMarksByExam(
            @PathVariable Long studentId, @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<MarksDTO> marks = marksService.getMarksByStudentAndExam(studentId, examId, pageNo, pageSize);
        return ResponseEntity.ok(marks);
    }

    @GetMapping("/student/{studentId}/average")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER') or @securityUtil.isCurrStudent(#studentId)")
    public ResponseEntity<Double> getAverageMarksForStudent(@PathVariable Long studentId) {
        Double average = marksService.getAverageMarksForStudent(studentId);
        return ResponseEntity.ok(average);
    }


    @GetMapping("/subject/{subjectId}/average")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<Double> getAverageMarksForSubject(@PathVariable Long subjectId) {
        Double average = marksService.getAverageMarksForSubject(subjectId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<Page<MarksDTO>> getMarksByExam(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<MarksDTO> marksByExam = marksService.getMarksByExam(examId, pageNo, pageSize);
            return ResponseEntity.ok(marksByExam);
    }


    @GetMapping("/subject/{subjectId}/top")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<List<MarksDTO>> getTopPerformingStudentsBySubject(
                @PathVariable Long subjectId,
                @RequestParam(defaultValue = "10") int limit) {
        List<MarksDTO> topStudents = marksService.getTopPerformingStudentsBySubject(subjectId, limit);
        return ResponseEntity.ok(topStudents);
    }
}