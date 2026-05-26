package com.example.demo.DTO;

import com.example.demo.Helper.StudentFeeStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFeeResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long classEntityId;
    private String className;
    private Long feeStructureId;
    private String feeStructureName;
    private Double totalAmount;
    private Double paidAmount;
    private Double remainingAmount;
    private LocalDate dueDate;
    private StudentFeeStatus status;
}