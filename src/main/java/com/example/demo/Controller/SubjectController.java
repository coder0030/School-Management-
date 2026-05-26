package com.example.demo.Controller;

import com.example.demo.DTO.SubjectDTO;
import com.example.demo.ENTITY.Subject;
import com.example.demo.RequestDTO.SubjectRequestDTO;
import com.example.demo.Service.SubjectService;
import com.example.demo.ServiceImpl.SubjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectRequestDTO subjectRequestDTO) {
        SubjectDTO subject = subjectService.createSubject(subjectRequestDTO);
        return ResponseEntity.ok(subject);
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        List<SubjectDTO> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        SubjectDTO subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @PutMapping("updateId/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @RequestBody SubjectRequestDTO subjectRequestDTO) {
        SubjectDTO subject = subjectService.updateSubjectById(id, subjectRequestDTO);
        return ResponseEntity.ok(subject);
    }

    @PatchMapping("partialUpdateId/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<SubjectDTO> partialUpdateSubjectById(@PathVariable Long id, @RequestBody SubjectRequestDTO subjectRequestDTO) {
        SubjectDTO subject = subjectService.partialUpdateSubjectById(id, subjectRequestDTO);
        return ResponseEntity.ok(subject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> deleteSubjectById(@PathVariable Long id) {
        String message = subjectService.deleteSubjectById(id);
        return ResponseEntity.ok(message);
    }
}