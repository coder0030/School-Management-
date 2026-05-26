package com.example.demo.Service;

import com.example.demo.DTO.BookIssueDTO;
import com.example.demo.RequestDTO.BookIssueRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface BookIssueService {
    BookIssueDTO createBookIssue(@Valid BookIssueRequestDTO requestDto);

    BookIssueDTO getBookIssueById(Long id);

    Page<BookIssueDTO> getActiveIssuedBooks(int pageNo, int pageSize);

    Page<BookIssueDTO> getReturnedBooks(int pageNo, int pageSize);

    List<BookIssueDTO> getIssuesByStudentId(Long studentId);

    List<BookIssueDTO> getIssuesByBookId(Long bookId);

    void returnBook(Long studentId, Long bookId);

    Long countActiveIssuedBooks();

    Boolean isBookCurrentlyIssued(Long bookId);

    Page<BookIssueDTO> getOverdueBooks(int pageNo, int pageSize);

    Page<BookIssueDTO> getAllIssuedBooks(int pageNo, int pageSize);

    void returnBookByIssueId(Long studentId, Long id);

    BookIssueDTO extendDueDate(Long id, LocalDate newDueDate);

    BookIssueDTO updateBookIssue(Long id, @Valid BookIssueRequestDTO requestDto);

    BookIssueDTO partialUpdateBookIssue(Long id, BookIssueRequestDTO requestDto);
}
