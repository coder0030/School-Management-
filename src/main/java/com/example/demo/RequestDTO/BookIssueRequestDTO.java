package com.example.demo.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookIssueRequestDTO {

    @NotNull(message = "Book id is required")
    private Long bookId;

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotBlank(message = "Status is required")
    private String status;

    private LocalDate returnDate; // optional during issue
    private Double fineAmount;    // optional during issue
}
