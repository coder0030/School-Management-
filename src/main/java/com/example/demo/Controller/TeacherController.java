package com.example.demo.Controller;

import com.example.demo.DTO.TeacherDTO;
import com.example.demo.ENTITY.Teacher;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.TeacherRequestDTO;
import com.example.demo.Service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherServiceImpl;

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/create")
    public ResponseEntity<TeacherDTO> createTeacher(@Valid @RequestBody TeacherRequestDTO teacherRequestDTO) {
        TeacherDTO created = teacherServiceImpl.createTeacher(teacherRequestDTO);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN') or @securityUtil.isCurrTeacher(#id)")
    @PutMapping("updateId/{teacherId}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Long teacherId,
                                                    @Valid @RequestBody TeacherRequestDTO requestDTO) {
        TeacherDTO updated = teacherServiceImpl.updateTeacher(teacherId, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("deleteId/{teacherId}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long teacherId) {
        teacherServiceImpl.deleteTeacher(teacherId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PatchMapping("partialUpdateId/{teacherId}")
    public ResponseEntity<TeacherDTO> patchTeacher(@PathVariable Long teacherId,
                                                   @RequestBody TeacherRequestDTO requestDTO) {
        TeacherDTO patched = teacherServiceImpl.partialUpdate(teacherId, requestDTO);
        return ResponseEntity.ok(patched);
    }

    @PreAuthorize("""
        hasAnyRole('ADMIN','SUPERADMIN')
        or @securityUtil.isCurrTeacher(#id)
    """)
    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long teacherId) {
        TeacherDTO teacherDTO = teacherServiceImpl.getTeacherById(teacherId);
        return ResponseEntity.ok(teacherDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        List<TeacherDTO> teacherDTOs = teacherServiceImpl.getAllTeacher();
        return ResponseEntity.ok(teacherDTOs);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','CLASSTEACHER')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<TeacherDTO>> getTeachersByClass(@PathVariable Long classId) {
        List<TeacherDTO> teacherDTOs = teacherServiceImpl.getTeacherByClass(classId);
        return ResponseEntity.ok(teacherDTOs);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/assign-subject/{teacherId}/{subjectId}/{classId}")
    public ResponseEntity<Void> assignTeacherSubjectToClass(@PathVariable Long teacherId,
                                                            @PathVariable Long subjectId,
                                                            @PathVariable Long classId) {
        teacherServiceImpl.assignTeacherSubjectToClass(teacherId, subjectId, classId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @DeleteMapping("/remove-subject/{teacherId}/{subjectId}/{classId}")
    public ResponseEntity<Void> removeTeacherSubjectToClass(@PathVariable Long teacherId,
                                                            @PathVariable Long subjectId,
                                                            @PathVariable Long classId) {
        teacherServiceImpl.removeTeacherSubjectToClass(teacherId, subjectId, classId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/{teacherId}/addRole")
    public ResponseEntity<TeacherDTO> addRole(@PathVariable Long teacherId, @RequestBody Role role) {
        TeacherDTO updatedTeacher = teacherServiceImpl.addRoleToTeacher(teacherId, role);
        return ResponseEntity.ok(updatedTeacher);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/{teacherId}/removeRole")
    public ResponseEntity<TeacherDTO> removeRole(@PathVariable Long teacherId, @RequestBody Role role) {
        TeacherDTO updatedTeacher = teacherServiceImpl.removeRoleFromTeacher(teacherId, role);
        return ResponseEntity.ok(updatedTeacher);
    }
}