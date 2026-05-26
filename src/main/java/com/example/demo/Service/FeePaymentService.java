package com.example.demo.Service;

import com.example.demo.DTO.BulkFeePaymentResponseDTO;
import com.example.demo.DTO.FeePaymentDTO;
import com.example.demo.RequestDTO.FeePaymentRequestDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface FeePaymentService {
    FeePaymentDTO payFee(@Valid FeePaymentRequestDTO requestDTO);

    FeePaymentDTO makePaymentByStudentId(@Valid FeePaymentRequestDTO requestDTO);

    FeePaymentDTO makePaymentByClassAndRollNumber(@Valid FeePaymentRequestDTO requestDTO);

    FeePaymentDTO makePaymentByMobileNumber(@Valid FeePaymentRequestDTO requestDTO);

    BulkFeePaymentResponseDTO makeBulkPaymentForStudentFee(Long studentFeeId, @Valid List<FeePaymentRequestDTO> requestDTOs);

    List<FeePaymentDTO> getPaymentsByStudentFeeId(Long studentFeeId);

    FeePaymentDTO getPaymentByReceiptNumber(String receiptNumber);
}
