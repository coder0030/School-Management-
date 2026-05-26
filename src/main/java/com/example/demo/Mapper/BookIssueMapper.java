package com.example.demo.Mapper;

import com.example.demo.DTO.BookIssueDTO;
import com.example.demo.ENTITY.BookIssue;
import com.example.demo.ENTITY.LibraryBook;
import com.example.demo.ENTITY.Student;
import com.example.demo.Helper.BookIssueStatus;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.RequestDTO.BookIssueRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookIssueMapper {

    // ENTITY → DTO
    public BookIssueDTO toDTO(BookIssue issue) {
        if (issue == null) return null;

        return BookIssueDTO.builder()
                .id(issue.getId())
                .issueDate(issue.getIssueDate())
                .dueDate(issue.getDueDate())
                .returnDate(issue.getReturnDate())
                .status(String.valueOf(issue.getStatus()))
                .fineAmount(issue.getFineAmount())

                .bookId(issue.getLibraryBook().getId())
                .bookTitle(issue.getLibraryBook().getTitle())

                .studentId(issue.getStudent().getId())
                .studentName(issue.getStudent().getFirstName())
                .build();
    }

    public BookIssue toUpdate(BookIssueRequestDTO dto, BookIssue issue) {

        if (dto == null) return null;

        if (dto.getIssueDate() == null ||
                dto.getDueDate() == null ||
                dto.getStatus() == null) {
            throw new IncompleteDataException("Required fields are missing.");
        }

        issue.setIssueDate(dto.getIssueDate());
        issue.setDueDate(dto.getDueDate());
        issue.setReturnDate(dto.getReturnDate());
        issue.setStatus(BookIssueStatus.valueOf(dto.getStatus()));
        issue.setFineAmount(dto.getFineAmount());

        return issue;
    }

    // PARTIAL UPDATE (mainly for return/fine/status)
    public BookIssue partialUpdate(BookIssueRequestDTO dto, BookIssue issue) {
        if (dto == null || issue == null) return null;

        if (dto.getIssueDate() != null)
            issue.setIssueDate(dto.getIssueDate());

        if (dto.getDueDate() != null)
            issue.setDueDate(dto.getDueDate());

        if (dto.getReturnDate() != null)
            issue.setReturnDate(dto.getReturnDate());

        if (dto.getStatus() != null)
            issue.setStatus(BookIssueStatus.valueOf(dto.getStatus()));

        if (dto.getFineAmount() != null)
            issue.setFineAmount(dto.getFineAmount());

        return issue;
    }

    public List<BookIssueDTO> toDTOList(List<BookIssue> issues) {
        if (issues == null || issues.isEmpty()) return null;
        return issues.stream().map(this::toDTO).toList();
    }
}