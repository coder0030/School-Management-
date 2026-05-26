package com.example.demo.Mapper;

import com.example.demo.DTO.LibraryBookDTO;
import com.example.demo.ENTITY.LibraryBook;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.RequestDTO.LibraryBookRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LibraryBookMapper {

    public LibraryBookDTO toDTO(LibraryBook libraryBook) {
        if (libraryBook == null) return null;

        return LibraryBookDTO.builder()
                .id(libraryBook.getId())
                .title(libraryBook.getTitle())
                .author(libraryBook.getAuthor())
                .publisher(libraryBook.getPublisher())
                .availableCopies(libraryBook.getAvailableCopies())
                .totalCopies(libraryBook.getTotalCopies())
                .build();
    }

    public LibraryBook toUpdate(LibraryBookRequestDTO requestDTO, LibraryBook libraryBook) {
        if (requestDTO == null) return null;

        if (requestDTO.getTitle() == null || requestDTO.getAuthor() == null ||
                requestDTO.getPublisher() == null || requestDTO.getTotalCopies() == null ||
                requestDTO.getAvailableCopies() == null) {

            throw new IncompleteDataException("Data is incompleted, Request Body is not filled.");
        }

        libraryBook.setTitle(requestDTO.getTitle());
        libraryBook.setAuthor(requestDTO.getAuthor());
        libraryBook.setPublisher(requestDTO.getPublisher());
        libraryBook.setTotalCopies(requestDTO.getTotalCopies());
        libraryBook.setAvailableCopies(requestDTO.getAvailableCopies());

        return libraryBook;
    }

    public LibraryBook partialUpdate(LibraryBookRequestDTO requestDTO, LibraryBook libraryBook) {
        if (requestDTO == null || libraryBook == null) return null;

        if (requestDTO.getTitle() != null) {
            libraryBook.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getAuthor() != null) {
            libraryBook.setAuthor(requestDTO.getAuthor());
        }
        if (requestDTO.getPublisher() != null) {
            libraryBook.setPublisher(requestDTO.getPublisher());
        }
        if (requestDTO.getTotalCopies() != null) {
            libraryBook.setTotalCopies(requestDTO.getTotalCopies());
        }
        if (requestDTO.getAvailableCopies() != null) {
            libraryBook.setAvailableCopies(requestDTO.getAvailableCopies());
        }

        return libraryBook;
    }

    public List<LibraryBookDTO> toDTOList(List<LibraryBook> libraryBooks) {
        if (libraryBooks == null || libraryBooks.isEmpty()) return null;

        return libraryBooks.stream().map(this::toDTO).toList();
    }
}