package com.example.demo.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookIssueDTO {

    private Long id;
    private Long bookId;
    private String bookTitle;

    private Long studentId;
    private String studentName;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private String status;
    private Double fineAmount;
}