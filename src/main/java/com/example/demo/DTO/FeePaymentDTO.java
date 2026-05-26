package com.example.demo.DTO;

import com.example.demo.Helper.PaymentMode;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeePaymentDTO {

    private Long studentFeeId;
    private String studentName;
    private String feeType;

    private Double totalAmount;
    private Double totalPaid;
    private Double remainingBalance;

    private String paymentStatus;     // UNPAID | PARTIAL | PAID

    private Long paymentId;
    private String receiptNumber;
    private Double amountPaid;
    private LocalDate paymentDate;
    private PaymentMode paymentMode;
}