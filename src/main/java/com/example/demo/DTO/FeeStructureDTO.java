package com.example.demo.DTO;

import com.example.demo.Helper.FeeType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructureDTO {
    private Long id;
    private FeeType feeType;
    private Double amount;
    private String frequency;
    private LocalDate dueDate;
    private Long classId;

}
