package com.example.demo.ServiceImpl;

import com.example.demo.DTO.LibraryBookDTO;
import com.example.demo.ENTITY.LibraryBook;
import com.example.demo.Mapper.LibraryBookMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.LibraryBookRepository;
import com.example.demo.RequestDTO.LibraryBookRequestDTO;
import com.example.demo.Service.LibraryBookService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryBookServiceImpl implements LibraryBookService {
    private final AllRepositoryMethods allRepositoryMethods;
    private final LibraryBookRepository libraryBookRepository;
    private final LibraryBookMapper libraryBookMapper;
    private final ModelMapper modelMapper;

    private void validationCheck(String title, String author) {
        if(title == null || author == null) {
            throw new BadRequestException("Book Title or Author can't be null.");
        }

        if(libraryBookRepository.existsByTitleAndAuthor(title,author)) {
            throw new BadRequestException("Book Title : " + title + " and Author : " +
                    author + " already exists.");
        }
    }

    @Transactional
    public LibraryBookDTO createLibraryBook(@Valid LibraryBookRequestDTO requestDto) {
        validationCheck(requestDto.getTitle(), requestDto.getAuthor());

        LibraryBook book = new LibraryBook();
        book = libraryBookMapper.toUpdate(requestDto,book);
        LibraryBook created = libraryBookRepository.save(book);

        return libraryBookMapper.toDTO(created);
    }

    public Page<LibraryBookDTO> getAllLibraryBooks(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<LibraryBook> bookListPage = libraryBookRepository.findAll(pageable);

        if(bookListPage.getContent().isEmpty()) {
            return Page.empty(pageable);         }

        return bookListPage.map(libraryBookMapper::toDTO);
    }

    public LibraryBookDTO getLibraryBookById(Long id) {
        LibraryBook book = allRepositoryMethods.getLibraryBookById(id);
        return libraryBookMapper.toDTO(book);
    }

    @Transactional
    public void deletedLibraryBookById(Long id) {
        LibraryBook book = allRepositoryMethods.getLibraryBookById(id);
        libraryBookRepository.delete(book);
    }

    @Override
    public List<LibraryBookDTO> searchLibraryBooks(String keyword) {
        List<LibraryBook> books = libraryBookRepository.findByTitleStartingWith(keyword);

        if(books.isEmpty()) {
            return List.of();
        }

        return libraryBookMapper.toDTOList(books);
    }

    @Override
    public Long countLibraryBooks() {
        return libraryBookRepository.count();
    }

    @Transactional
    public LibraryBookDTO updateLibraryBook(Long id, @Valid LibraryBookRequestDTO requestDto) {

        LibraryBook book = allRepositoryMethods.getLibraryBookById(id);

        if(libraryBookRepository.existsByTitleAndAuthorAndIdNot(requestDto.getTitle(),
                requestDto.getAuthor(), id)) {
            throw new BadRequestException("Book Title : " + requestDto.getTitle() +
                            " and Author : " + requestDto.getAuthor() + " already exists.");
        }

        book = libraryBookMapper.toUpdate(requestDto, book);
        LibraryBook updated = libraryBookRepository.save(book);
        return libraryBookMapper.toDTO(updated);
    }

    @Transactional
    public LibraryBookDTO partialUpdateLibraryBook(Long id, @Valid LibraryBookRequestDTO requestDto) {
        LibraryBook book = allRepositoryMethods.getLibraryBookById(id);
        String authorName = book.getAuthor(), title = book.getTitle();

        if(requestDto.getAuthor() != null && !requestDto.getAuthor().equals(book.getAuthor())) {
            authorName = requestDto.getAuthor();
        }

        if(requestDto.getTitle() != null && !requestDto.getTitle().equals(book.getTitle())) {
            title = requestDto.getTitle();
        }

        if(!authorName.equals(book.getAuthor()) || !title.equals(book.getTitle())) {
            validationCheck(title, authorName);
        }

        book.setAuthor(authorName);
        book.setTitle(title);

        book = libraryBookMapper.partialUpdate(requestDto,book);
        LibraryBook updated = libraryBookRepository.save(book);
        return libraryBookMapper.toDTO(updated);
    }
}
