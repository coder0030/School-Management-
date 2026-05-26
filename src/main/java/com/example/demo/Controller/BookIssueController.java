package com.example.demo.Controller;

import com.example.demo.DTO.BookIssueDTO;
import com.example.demo.RequestDTO.BookIssueRequestDTO;
import com.example.demo.Service.BookIssueService;
import com.example.demo.ServiceImpl.BookIssueServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/book-issue")
@RequiredArgsConstructor
public class BookIssueController {

    private final BookIssueService bookIssueService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<BookIssueDTO> createBookIssue(
            @Valid @RequestBody BookIssueRequestDTO requestDto) {

        BookIssueDTO created = bookIssueService.createBookIssue(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<BookIssueDTO> getBookIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(bookIssueService.getBookIssueById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<Page<BookIssueDTO>> getActiveIssuedBooks(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(bookIssueService.getActiveIssuedBooks(pageNo, pageSize));
    }

    @GetMapping("/returned")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<Page<BookIssueDTO>> getReturnedBooks(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(bookIssueService.getReturnedBooks(pageNo, pageSize));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("""
              hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')
              or @securityUtil.isCurrStudent(#studentId)
    """)
    public ResponseEntity<List<BookIssueDTO>> getIssuesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(bookIssueService.getIssuesByStudentId(studentId));
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<List<BookIssueDTO>> getIssuesByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookIssueService.getIssuesByBookId(bookId));
    }

    @DeleteMapping("/return")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<String> returnBook(@RequestParam Long studentId, @RequestParam Long bookId) {

        bookIssueService.returnBook(studentId, bookId);
        return ResponseEntity.ok("Book returned successfully.");
    }

    @GetMapping("/count/active")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<Long> countActiveIssuedBooks() {
        return ResponseEntity.ok(bookIssueService.countActiveIssuedBooks());
    }


    @GetMapping("/is-issued/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<Boolean> isBookIssued(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookIssueService.isBookCurrentlyIssued(bookId));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<Page<BookIssueDTO>> getOverdueBooks(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(bookIssueService.getOverdueBooks(pageNo, pageSize));
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<Page<BookIssueDTO>> getAllBookIssues(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<BookIssueDTO> list = bookIssueService.getAllIssuedBooks(pageNo, pageSize);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("/studentId/{studentId}/bookId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> returnBookIssueById(@PathVariable Long studentId, @PathVariable Long id) {
        bookIssueService.returnBookByIssueId(studentId, id);
        return ResponseEntity.ok("Book Issue record " + id + " deleted successfully.");
    }

    public ResponseEntity<BookIssueDTO> extendDueDate(@PathVariable Long id, @PathVariable LocalDate newDueDate) {
        BookIssueDTO bookIssueDTO = bookIssueService.extendDueDate(id,newDueDate);
        return ResponseEntity.ok(bookIssueDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<BookIssueDTO> updateBookIssue(
            @PathVariable Long id,
            @Valid @RequestBody BookIssueRequestDTO requestDto) {

        BookIssueDTO updated = bookIssueService.updateBookIssue(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','LIBRARIAN')")
    public ResponseEntity<BookIssueDTO> partialUpdateBookIssue(
            @PathVariable Long id,
            @RequestBody BookIssueRequestDTO requestDto) {

        BookIssueDTO updated = bookIssueService.partialUpdateBookIssue(id, requestDto);
        return ResponseEntity.ok(updated);
    }


}