package com.example.demo.DTO;

import com.example.demo.RequestDTO.FeePaymentRequestDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkFeePaymentResponseDTO {
    List<FeePaymentRequestDTO> skippedStudents;
    List<FeePaymentDTO> successfulStudents;
    String remarks;
}
