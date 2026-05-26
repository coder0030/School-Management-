package com.example.demo.Controller;

import com.example.demo.DTO.BulkFeePaymentResponseDTO;
import com.example.demo.DTO.FeePaymentDTO;
import com.example.demo.RequestDTO.FeePaymentRequestDTO;
import com.example.demo.Service.FeePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fee-payments")
@RequiredArgsConstructor
public class FeePaymentController {

    private final FeePaymentService feePaymentService;

    @PostMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT') and " +
            "@securityUtil.isCurrStudent(#studentId)")
    public ResponseEntity<FeePaymentDTO> makePaymentByStudentId(
            @PathVariable Long studentId,
            @Valid @RequestBody FeePaymentRequestDTO requestDTO) {

        requestDTO.setStudentId(studentId);
        FeePaymentDTO payment = feePaymentService.makePaymentByStudentId(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/class/{classId}/roll/{rollNumber}")
    @PreAuthorize("""
        hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT') or
        @securityUtil.isCurrStudentByClassAndRoll(#classId, #rollNumber)
        """)
    public ResponseEntity<FeePaymentDTO> makePaymentByClassAndRollNumber(
            @PathVariable Long classId,
            @PathVariable Integer rollNumber,
            @Valid @RequestBody FeePaymentRequestDTO requestDTO) {

        requestDTO.setClassId(classId);
        requestDTO.setRollNumber(rollNumber);

        FeePaymentDTO payment = feePaymentService.makePaymentByClassAndRollNumber(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/mobile/{mobileNo}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','ACCOUNTANT') and " +
            "@securityUtil.isCurrStudentByPhone(#mobileNo)")
    public ResponseEntity<FeePaymentDTO> makePaymentByMobileNumber(
            @PathVariable String mobileNo,
            @Valid @RequestBody FeePaymentRequestDTO requestDTO) {

        requestDTO.setMobileNumber(mobileNo);
        FeePaymentDTO payment = feePaymentService.makePaymentByMobileNumber(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/student-fee/{studentFeeId}/bulk-payment")
    @PreAuthorize("""
        hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('ACCOUNTANT')
        or @securityUtil.isCurrParentOfStudentFee(#studentFeeId)
        """)
    public ResponseEntity<BulkFeePaymentResponseDTO> makeBulkPaymentForStudentFee(
            @PathVariable Long studentFeeId,
            @Valid @RequestBody List<FeePaymentRequestDTO> requestDTOs) {

        BulkFeePaymentResponseDTO payments = feePaymentService.makeBulkPaymentForStudentFee(studentFeeId, requestDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(payments);
    }

    @GetMapping("/student-fee/{studentFeeId}")
    @PreAuthorize("""
        hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('ACCOUNTANT')
        or @securityUtil.isCurrParentOfStudentFee(#studentFeeId)
        """)
    public ResponseEntity<List<FeePaymentDTO>> getPaymentsByStudentFeeId(
            @PathVariable Long studentFeeId) {

        List<FeePaymentDTO> payments = feePaymentService.getPaymentsByStudentFeeId(studentFeeId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/receipt/{receiptNumber}")
    @PreAuthorize("""
        hasAnyRole('SUPERADMIN','ADMIN','ACCOUNTANT','PARENT','TEACHER','CLASSTEACHER')
        or @securityUtil.isCurrStudentByReceipt(#studentFeeId)
        """)
    public ResponseEntity<FeePaymentDTO> getPaymentByReceiptNumber(
            @PathVariable String receiptNumber) {

        FeePaymentDTO payment = feePaymentService.getPaymentByReceiptNumber(receiptNumber);
        return ResponseEntity.ok(payment);
    }
}