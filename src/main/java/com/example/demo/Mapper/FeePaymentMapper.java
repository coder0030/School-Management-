package com.example.demo.Mapper;


import com.example.demo.DTO.FeePaymentDTO;
import com.example.demo.ENTITY.FeePayment;
import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.StudentFee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Fixed FeePaymentMapper.java
@Component
@RequiredArgsConstructor
@Slf4j
public class FeePaymentMapper {

    public FeePaymentDTO toDTO(FeePayment feePayment) {
        return FeePaymentDTO.builder()
                .studentFeeId(feePayment.getStudentFee().getId())
                .studentName(feePayment.getStudent().getStudentName())
                .totalAmount(feePayment.getStudentFee().getTotalAmount())
                .totalPaid(feePayment.getStudentFee().getPaidAmount())
                .remainingBalance(feePayment.getStudentFee().getRemainingAmount())
                .paymentStatus(feePayment.getStatus())
                .paymentId(feePayment.getId())
                .receiptNumber(feePayment.getReceiptNumber())
                .amountPaid(feePayment.getAmountPaid())
                .paymentDate(feePayment.getPaymentDate())
                .paymentMode(feePayment.getPaymentMode())
                .build();
    }

    public List<FeePaymentDTO> toDTOList(List<FeePayment> feePayments) {

        if (feePayments == null || feePayments.isEmpty()) {
            return List.of();
        }
        return feePayments.stream().map(this::toDTO).toList();
    }
}