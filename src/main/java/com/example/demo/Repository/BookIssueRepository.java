package com.example.demo.Repository;

import com.example.demo.ENTITY.BookIssue;
import com.example.demo.Helper.BookIssueStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    Optional<BookIssue> findByIdAndStudent_Id(Long id, Long studentId);

    Page<BookIssue> findAllByStatus(BookIssueStatus status, Pageable pageable);

    boolean existsByLibraryBook_IdAndStudent_Id(Long bookId, Long studentId);

    boolean existsByLibraryBook_IdAndStudent_IdAndIdNot(Long bookId, Long studentId, Long id);

    Page<BookIssue> findAllByStatusAndReturnDateBetween(
            BookIssueStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<BookIssue> findAllByStudent_IdAndStatus(Long studentId, BookIssueStatus status);

    List<BookIssue> findAllByLibraryBook_Id(Long bookId);

    Optional<BookIssue> findByStudent_IdAndLibraryBook_IdAndStatus(
            Long studentId, Long bookId, BookIssueStatus status);

    Long countByStatus(BookIssueStatus status);

    Boolean existsByLibraryBook_IdAndStatus(Long bookId, BookIssueStatus status);

    Page<BookIssue> findAllByStatusAndDueDateBefore(BookIssueStatus status, LocalDate today, Pageable pageable);
}