package com.example.demo.Controller;

import com.example.demo.DTO.ClassTeacherDTO;
import com.example.demo.RequestDTO.ClassTeacherRequestDTO;
import com.example.demo.Service.ClassTeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/class-teacher")
@RequiredArgsConstructor
public class ClassTeacherController {

    private final ClassTeacherService classTeacherService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<ClassTeacherDTO> assignTeacherToClass(
            @Valid @RequestBody ClassTeacherRequestDTO requestDto) {
        ClassTeacherDTO created = classTeacherService.assignTeacherToClass(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<ClassTeacherDTO>> getAllClassTeachers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ClassTeacherDTO> list = classTeacherService.getAllClassTeachers(pageNo, pageSize);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ClassTeacherDTO> getClassTeacherById(@PathVariable Long id) {
        ClassTeacherDTO dto = classTeacherService.getClassTeacherById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClassTeacher(@PathVariable Long id) {
        classTeacherService.deleteClassTeacher(id);
        return ResponseEntity.ok("Class-Teacher relationship successfully deleted");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeTeacherFromClass(
            @RequestParam Long classId,
            @RequestParam Long teacherId) {
        classTeacherService.removeTeacherFromClass(classId, teacherId);
        return ResponseEntity.ok("Teacher successfully removed from class");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER','CLASSTEACHER')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ClassTeacherDTO>> getTeachersByClassId(@PathVariable Long classId) {
        List<ClassTeacherDTO> list = classTeacherService.getTeachersByClassId(classId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','TEACHER','CLASSTEACHER')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassTeacherDTO>> getClassesByTeacherId(@PathVariable Long teacherId) {
        List<ClassTeacherDTO> list = classTeacherService.getClassesByTeacherId(teacherId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}