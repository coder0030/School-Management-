package com.example.demo.ENTITY;

import com.example.demo.Helper.BookIssueStatus;
import com.example.demo.Helper.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BookIssue")
public class BookIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private BookIssueStatus status;

    @Column(name = "fine_amount")
    private Double fineAmount;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private LibraryBook libraryBook;

    @PrePersist
    protected void create() {
        issueDate = LocalDate.now();
        status = BookIssueStatus.ISSUED;
        dueDate = LocalDate.now().plusDays(10);
        fineAmount = 0.0;
    }
}
