package com.example.demo.RequestDTO;

import com.example.demo.Helper.PaymentMode;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeePaymentRequestDTO {

    @NotNull(message = "StudentFee id is required")
    private Long studentFeeId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than zero")
    private Double amountPaid;

    @NotNull(message = "Payment mode is required")
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String feeType;

    private String transactionId;

    private Long studentId;
    private Long classId;
    private Integer rollNumber;
    private String mobileNumber;
}