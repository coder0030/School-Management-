package com.example.demo.Controller;

import com.example.demo.DTO.ClassDTO;
import com.example.demo.RequestDTO.ClassRequestDTO;
import com.example.demo.Service.ClassService;
import com.example.demo.ServiceImpl.ClassServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','STUDENT','PARENT')")
public class ClassController {

    private final ClassService classService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ClassDTO> createClass(@Valid @RequestBody ClassRequestDTO classRequestDTO) {
        ClassDTO createdClass = classService.createClass(classRequestDTO);
        return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ClassDTO>> getAllClasses(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ClassDTO> classes = classService.getAllClasses(pageNo, pageSize);
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ClassDTO> getClassById(@PathVariable Long id) {
        ClassDTO classDTO = classService.getClassById(id);
        return new ResponseEntity<>(classDTO, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")

    public ResponseEntity<Void> deleteClassById(@PathVariable Long id) {
        classService.deleteClassById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ClassDTO> updateClassById(@PathVariable Long id, @RequestBody ClassRequestDTO classRequestDTO) {
        ClassDTO updatedClass = classService.updateClassById(id, classRequestDTO);
        return new ResponseEntity<>(updatedClass, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ClassDTO> partialUpdateClassById(@PathVariable Long id, @RequestBody ClassRequestDTO classRequestDTO) {
        ClassDTO updatedClass = classService.partialUpdateClassById(id, classRequestDTO);
        return new ResponseEntity<>(updatedClass, HttpStatus.OK);
    }
}