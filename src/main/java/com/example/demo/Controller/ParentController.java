package com.example.demo.Controller;

import com.example.demo.DTO.ParentResponseDTO;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.RequestDTO.ParentRequestDTO;
import com.example.demo.Service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ParentResponseDTO> createParent(@Valid @RequestBody ParentRequestDTO requestDTO) {
        ParentResponseDTO response = parentService.createParent(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("""
          hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')
          or @securityUtil.isCurrParent(#id)
          """)
    public ResponseEntity<ParentResponseDTO> getParentById(@PathVariable Long id) {
        ParentResponseDTO response = parentService.getParentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{parentCode}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<ParentResponseDTO> getParentByCode(@PathVariable String parentCode) {
        ParentResponseDTO response = parentService.getParentByCode(parentCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')")
    public ResponseEntity<ParentResponseDTO> getParentByEmail(@PathVariable String email) {
        ParentResponseDTO response = parentService.getParentByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Page<ParentResponseDTO>> getAllParents(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ParentResponseDTO> responses = parentService.getAllParents(pageNo, pageSize);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/multiple-children")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Page<ParentResponseDTO>> getParentsWithMultipleChildren(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ParentResponseDTO> responses = parentService.getParentsWithMultipleChildren(pageNo, pageSize);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("updateId/{id}")
    @PreAuthorize("""
         hasAnyRole('SUPERADMIN','ADMIN')
         or @securityUtil.isCurrParent(#id)
         """)
    public ResponseEntity<ParentResponseDTO> updateParent(
            @PathVariable Long id,
            @Valid @RequestBody ParentRequestDTO requestDTO) {
        ParentResponseDTO response = parentService.updateParent(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("partialUpdateId/{id}")
    @PreAuthorize("""
         hasAnyRole('SUPERADMIN','ADMIN')
         or @securityUtil.isCurrParent(#id)
         """)
    public ResponseEntity<ParentResponseDTO> partialUpdateParent(
            @PathVariable Long id,
            @Valid @RequestBody ParentRequestDTO requestDTO) {
        ParentResponseDTO response = parentService.partialUpdateParent(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("deleteId/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("parent/{parentId}/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ParentResponseDTO> addStudentToParent(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {
        ParentResponseDTO response = parentService.addStudentToParent(parentId, studentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{parentId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<ParentResponseDTO> removeStudentFromParent(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {
        ParentResponseDTO response = parentService.removeStudentFromParent(parentId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{parentId}/children")
    @PreAuthorize("""
           hasAnyRole('SUPERADMIN','ADMIN','TEACHER','CLASSTEACHER')
           or @securityUtil.isCurrParent(#parentId)
           """)
    public ResponseEntity<List<StudentDTO>> getAllChildrenOfParent(@PathVariable Long parentId) {
        List<StudentDTO> responses = parentService.getAllChildrenOfParent(parentId);
        return ResponseEntity.ok(responses);
    }
}