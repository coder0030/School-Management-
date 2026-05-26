package com.example.demo.Controller;

import com.example.demo.DTO.ClassSubjectDTO;
import com.example.demo.RequestDTO.ClassSubjectRequestDTO;
import com.example.demo.RequestDTO.TeacherSubjectRequestDTO;
import com.example.demo.Service.ClassSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/class-subject")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','STUDENT','PARENT')")
public class ClassSubjectController {

    private final ClassSubjectService classSubjectService;

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ClassSubjectDTO> assignSubjectToClass(@Valid @RequestBody ClassSubjectRequestDTO requestDto) {
        ClassSubjectDTO created = classSubjectService.assignSubjectToClass(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ClassSubjectDTO>> getAllClassSubjects(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ClassSubjectDTO> list = classSubjectService.getAllClassSubjects(pageNo, pageSize);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassSubjectDTO> getClassSubjectById(@PathVariable Long id) {
        ClassSubjectDTO dto = classSubjectService.getClassSubjectById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> deleteClassSubject(@PathVariable Long id) {
        classSubjectService.deleteClassSubject(id);
        return ResponseEntity.ok("Successfully Deleted");
    }

    @DeleteMapping("/remove/{classSubjectId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> removeSubjectFromClassById(@PathVariable Long classSubjectId) {
        classSubjectService.removeSubjectFromClassById(classSubjectId);
        return ResponseEntity.ok("Subject successfully removed from class");
    }

    @DeleteMapping("/classes/{classId}/subjects/{subjectId}/exists")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> removeSubjectFromClass(
            @PathVariable Long classId,
            @PathVariable Long subjectId) {
        classSubjectService.removeSubjectFromClass(classId, subjectId);
        return ResponseEntity.ok("Subject successfully removed from class");
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ClassSubjectDTO>> getClassSubjectsBySubjectId(@PathVariable Long subjectId) {
        List<ClassSubjectDTO> list = classSubjectService.getClassSubjectsBySubjectId(subjectId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/classes/{classId}/subjects/{subjectId}/exists")
    public ResponseEntity<Boolean> isSubjectAssignedToClass(
            @PathVariable  Long classId,
            @PathVariable  Long subjectId) {
        boolean exists = classSubjectService.isSubjectAssignedToClass(classId, subjectId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ClassSubjectDTO> updateClassSubject(
            @PathVariable Long id,
            @Valid @RequestBody ClassSubjectRequestDTO requestDto){
        ClassSubjectDTO result = classSubjectService.assignOrUpdateSubjectToClass(id,requestDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ClassSubjectDTO> partialUpdateClassSubject(
            @PathVariable Long id,
            @Valid @RequestBody ClassSubjectRequestDTO requestDto){
        ClassSubjectDTO result = classSubjectService.partialUpdateClassSubject(id,requestDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/bulk-assign/classId/{classId}")
    public ResponseEntity<List<ClassSubjectDTO>> bulkAssignSubjectsToClass
            (@PathVariable  Long classId,
             @RequestBody List<TeacherSubjectRequestDTO> requestDTOS) {
        List<ClassSubjectDTO> results = classSubjectService.bulkAssignSubjectsToClass(classId, requestDTOS);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/bulk-remove/classId/{classId}")
    public ResponseEntity<String> bulkRemoveSubjectsFromClass
            (@PathVariable  Long classId,
             @RequestBody List<TeacherSubjectRequestDTO> requestDTOS) {
        classSubjectService.bulkRemoveSubjectsFromClass(classId, requestDTOS);
        return ResponseEntity.ok("Deleted successfully.");
    }


    @GetMapping("/class/{classId}/paged")
    public ResponseEntity<Map<String, Object>> getClassSubjectsByClassIdPaged(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = classSubjectService.getClassSubjectsByClassIdPaged(classId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Deprecated
    @PostMapping("/remove-old/{classSubjectId}")
    public ResponseEntity<ClassSubjectDTO> removeClassSubjectToClass(
            @PathVariable Long classSubjectId,
            @Valid @RequestBody ClassSubjectRequestDTO requestDTO) {
        ClassSubjectDTO removed = classSubjectService.removeClassSubjectToClass(classSubjectId, requestDTO);
        return ResponseEntity.ok(removed);
    }
}