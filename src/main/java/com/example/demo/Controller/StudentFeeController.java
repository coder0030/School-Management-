package com.example.demo.Controller;
import com.example.demo.DTO.FeePaymentDTO;
import com.example.demo.DTO.StudentFeeResponseDTO;
import com.example.demo.RequestDTO.FeePaymentRequestDTO;
import com.example.demo.RequestDTO.StudentFeeRequestDTO;
import com.example.demo.Service.StudentFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-fees")
@RequiredArgsConstructor
public class StudentFeeController {

    private final StudentFeeService studentFeeService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')")
    public ResponseEntity<StudentFeeResponseDTO> createStudentFee(@Valid @RequestBody StudentFeeRequestDTO requestDto) {
        StudentFeeResponseDTO createdFee = studentFeeService.createStudentFee(requestDto);
        return new ResponseEntity<>(createdFee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("""
    hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')
    or @securityUtil.isCurrStudent(#id)
    """)
    public ResponseEntity<StudentFeeResponseDTO> getStudentFeeById(@PathVariable Long id) {
        StudentFeeResponseDTO studentFee = studentFeeService.getStudentFeeById(id);
        return ResponseEntity.ok(studentFee);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')")
    public ResponseEntity<List<StudentFeeResponseDTO>> getAllStudentFees() {
        List<StudentFeeResponseDTO> studentFees = studentFeeService.getAllStudentFees();
        return ResponseEntity.ok(studentFees);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')")
    public ResponseEntity<StudentFeeResponseDTO> updateStudentFee(
            @PathVariable Long id,
            @Valid @RequestBody StudentFeeRequestDTO requestDto) {
        StudentFeeResponseDTO updatedFee = studentFeeService.updateStudentFee(id, requestDto);
        return ResponseEntity.ok(updatedFee);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("""
    hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')
    or @securityUtil.isCurrStudent(#studentId)
    """)
    public ResponseEntity<List<StudentFeeResponseDTO>> getFeesByStudentId(@PathVariable Long studentId) {
        List<StudentFeeResponseDTO> fees = studentFeeService.getFeesByStudentId(studentId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')")
    public ResponseEntity<List<StudentFeeResponseDTO>> getFeesByClassId(@PathVariable Long classId) {
        List<StudentFeeResponseDTO> fees = studentFeeService.getFeesByClassId(classId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/class/{classId}/roll/{rollNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')")
    public ResponseEntity<List<StudentFeeResponseDTO>> payFeeByClassAndRoll(@PathVariable Long classId, @PathVariable Integer rollNumber) {
        List<StudentFeeResponseDTO> studentFeeResponseDTO = studentFeeService.getFeeByStudentClassAndRollNo(classId, rollNumber);
        return ResponseEntity.ok(studentFeeResponseDTO);
    }

    @GetMapping("/mobile/{mobileNo}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT')")
    public ResponseEntity<List<StudentFeeResponseDTO>> payFeeByMobile(
            @PathVariable String mobileNo,
            @Valid @RequestBody StudentFeeRequestDTO requestDTO) {

        List<StudentFeeResponseDTO> studentFeeResponseDTO = studentFeeService.getFeeByStudentMobile(mobileNo, requestDTO);
        return ResponseEntity.ok(studentFeeResponseDTO);
    }
}