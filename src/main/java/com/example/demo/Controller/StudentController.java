package com.example.demo.Controller;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.DTO.TeacherDTO;
import com.example.demo.RequestDTO.StudentRequestDTO;
import com.example.demo.RequestDTO.TeacherRequestDTO;
import com.example.demo.Service.StudentService;
import com.example.demo.ServiceImpl.StudentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentRequestDTO studentRequestDTO) {
        log.info("StudentData received...");
        StudentDTO created = studentService.createStudent(studentRequestDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("""
    hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')
    or @securityUtil.isCurrStudent(#id)
    """)
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO created = studentService.getStudentById(id);
        return ResponseEntity.ok(created);
    }

    @GetMapping("classId/{classId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<List<StudentDTO>> getAllStudents(Long classId) {
        List<StudentDTO> list = studentService.getAllStudents(classId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<List<StudentDTO>> getAllStudent() {
        List<StudentDTO> created = studentService.getAllStudent();
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Void> deleteStudentById(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("updateId/{id}")
    @PreAuthorize("""
    hasAnyRole('SUPERADMIN','ADMIN')
    or @securityUtil.isCurrStudent(#id)
    """)
    public ResponseEntity<StudentDTO> updateStudentById(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO studentRequestDTO) throws AccessDeniedException {
        StudentDTO studentDTO = studentService.updateStudentById(id,studentRequestDTO);
        return ResponseEntity.ok(studentDTO);
    }

    @PatchMapping("partialUpdate/{id}")
    @PreAuthorize("""
    hasAnyRole('SUPERADMIN','ADMIN')
    or @securityUtil.isCurrStudent(#id)
    """)
    public ResponseEntity<StudentDTO> patchStudentUpdateById(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO studentRequestDTO) throws AccessDeniedException {
        StudentDTO studentDTO = studentService.patchUpdateStudentById(id,studentRequestDTO);
        return ResponseEntity.ok(studentDTO);
    }

        @PostMapping("/assign/{studentId}")
        @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
        public ResponseEntity<StudentDTO> assignStudentToClass(@PathVariable Long studentId, @Valid @RequestBody StudentRequestDTO requestDto) {
            StudentDTO created = studentService.assignStudentToClass(studentId,requestDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        }

        @GetMapping("/classStudent/all")
        @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','CLASSTEACHER')")
        public ResponseEntity<List<StudentDTO>> getAllClassStudents() {
            List<StudentDTO> list = studentService.getAllClassStudents();
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

        @GetMapping("/classStudent/{id}")
        @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','CLASSTEACHER')")
        public ResponseEntity<StudentDTO> getClassStudentById(@PathVariable Long id) {
            StudentDTO dto = studentService.getClassStudentById(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }

        @DeleteMapping("/delete/{id}")
        @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
        public ResponseEntity<String> deleteClassStudent(@PathVariable Long id) {
            studentService.deleteClassStudent(id);
            return ResponseEntity.ok("Class-Student relationship successfully deleted");
        }

        @DeleteMapping("/remove")
        @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
        public ResponseEntity<String> removeStudentFromClass(
                @RequestParam Long classId,
                @RequestParam Long studentId) {
            studentService.removeStudentFromClass(classId, studentId);
            return ResponseEntity.ok("Student successfully removed from class");
        }

        @GetMapping("/class/{classId}")
        @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
        public ResponseEntity<List<StudentDTO>> getStudentsByClassId(@PathVariable Long classId) {
            List<StudentDTO> list = studentService.getStudentsByClassId(classId);
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER') or @securityUtil.isCurrStudent(#studentId)")
    public ResponseEntity<List<StudentDTO>> getClassesByStudentId(@PathVariable Long studentId) {
        List<StudentDTO> list = studentService.getClassesByStudentId(studentId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
