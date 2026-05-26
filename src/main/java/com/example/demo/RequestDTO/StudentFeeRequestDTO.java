package com.example.demo.RequestDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFeeRequestDTO {
    private Long studentId;
    private Long classEntityId;
    private Long feeStructureId;
    private Double totalAmount;
    private Double paidAmount;
    private Double remainingAmount;
    private LocalDate dueDate;
    private String status;
    private String phone;
}