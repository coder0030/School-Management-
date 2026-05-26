package com.example.demo.Service;

import com.example.demo.DTO.LibraryBookDTO;
import com.example.demo.RequestDTO.LibraryBookRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LibraryBookService {
    LibraryBookDTO createLibraryBook(@Valid LibraryBookRequestDTO requestDto);

    LibraryBookDTO getLibraryBookById(Long id);

    LibraryBookDTO updateLibraryBook(Long id, @Valid LibraryBookRequestDTO requestDto);

    LibraryBookDTO partialUpdateLibraryBook(Long id, @Valid LibraryBookRequestDTO requestDto);

    void deletedLibraryBookById(Long id);

    List<LibraryBookDTO> searchLibraryBooks(String keyword);

    Long countLibraryBooks();

    Page<LibraryBookDTO> getAllLibraryBooks(int pageNo, int pageSize);
}
