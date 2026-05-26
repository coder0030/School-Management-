package com.example.demo.Controller;

import com.example.demo.DTO.LibraryBookDTO;
import com.example.demo.MyException.ResourceNotFoundException;
import com.example.demo.RequestDTO.LibraryBookRequestDTO;
import com.example.demo.Service.LibraryBookService;
import com.example.demo.ServiceImpl.LibraryBookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library-book")
@RequiredArgsConstructor
public class LibraryBookController {
    private final LibraryBookService libraryBookService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','LIBRARIAN')")
    public ResponseEntity<LibraryBookDTO> createLibraryBook(@Valid @RequestBody LibraryBookRequestDTO requestDto) {
        LibraryBookDTO created = libraryBookService.createLibraryBook(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','LIBRARIAN','TEACHER','STUDENT')")
    public ResponseEntity<Page<LibraryBookDTO>> getAllLibraryBooks(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<LibraryBookDTO> list = libraryBookService.getAllLibraryBooks(pageNo, pageSize);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','LIBRARIAN','TEACHER','STUDENT')")
    public ResponseEntity<LibraryBookDTO> getLibraryBookById(@PathVariable Long id) {
        LibraryBookDTO list = libraryBookService.getLibraryBookById(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','LIBRARIAN')")
    public ResponseEntity<LibraryBookDTO> updateLibraryBook(@PathVariable Long id, @Valid @RequestBody LibraryBookRequestDTO requestDto) {
        LibraryBookDTO created = libraryBookService.updateLibraryBook(id,requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','LIBRARIAN')")
    public ResponseEntity<LibraryBookDTO> partialUpdateLibraryBook(@PathVariable Long id, @Valid @RequestBody LibraryBookRequestDTO requestDto) {
        LibraryBookDTO created = libraryBookService.partialUpdateLibraryBook(id,requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<String> deleteLibraryBookById(@PathVariable Long id) {
        libraryBookService.deletedLibraryBookById(id);
        return ResponseEntity.ok("Library Book " + id + ", Deleted Successfully.");
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','LIBRARIAN','TEACHER','STUDENT')")
    public ResponseEntity<List<LibraryBookDTO>> searchLibraryBooks(@RequestParam String keyword) {
        List<LibraryBookDTO> result = libraryBookService.searchLibraryBooks(keyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','LIBRARIAN')")
    public ResponseEntity<Long> countLibraryBooks() {
        Long count = libraryBookService.countLibraryBooks();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }


}
