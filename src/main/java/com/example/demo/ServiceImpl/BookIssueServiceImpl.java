package com.example.demo.ServiceImpl;

import com.example.demo.DTO.BookIssueDTO;
import com.example.demo.ENTITY.BookIssue;
import com.example.demo.ENTITY.LibraryBook;
import com.example.demo.ENTITY.Student;
import com.example.demo.Helper.BookIssueStatus;
import com.example.demo.Mapper.BookIssueMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.BookIssueRepository;
import com.example.demo.Repository.LibraryBookRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.RequestDTO.BookIssueRequestDTO;
import com.example.demo.Service.AttendanceService;
import com.example.demo.Service.BookIssueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookIssueServiceImpl implements BookIssueService {

    private final BookIssueRepository bookIssueRepository;
    private final AllRepositoryMethods allRepositoryMethods;
    private final BookIssueMapper bookIssueMapper;
    private final LibraryBookRepository libraryBookRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    private static final double FINE_PER_DAY = 10.0;

    private void checkValidation(Long bookId, Long studentId) {
        if (bookId == null || studentId == null) {
            throw new BadRequestException("Student id or book id can't be null");
        }

        if (!libraryBookRepository.existsById(bookId)) {
            throw new BadRequestException("Book id not exists");
        }

        if (!studentRepository.existsById(studentId)) {
            throw new BadRequestException("Student id not exists");
        }
    }

    private void checkForDuplicate(Long bookId, Long studentId) {
        if (bookIssueRepository.existsByLibraryBook_IdAndStudent_Id(bookId, studentId)) {
            throw new BadRequestException("Student id : " + studentId + " has already assigned this book");
        }
    }

    private void assignBookToStudent(BookIssue bookIssue, Student student, LibraryBook libraryBook) {
        if (libraryBook.getAvailableCopies() < 1) {
            throw new RuntimeException("Book not available currently");
        }

        libraryBook.addBookIssue(bookIssue);
        student.addBookIssue(bookIssue);

        libraryBook.setAvailableCopies(libraryBook.getAvailableCopies() - 1);
        libraryBookRepository.save(libraryBook);
    }

    @Transactional
    public BookIssueDTO createBookIssue(BookIssueRequestDTO requestDto) {
        checkValidation(requestDto.getBookId(), requestDto.getStudentId());
        Student student = allRepositoryMethods.getStudentById(requestDto.getStudentId());
        LibraryBook libraryBook = allRepositoryMethods.getLibraryBookById(requestDto.getBookId());

        checkForDuplicate(requestDto.getBookId(), requestDto.getStudentId());

        BookIssue bookIssue = new BookIssue();
        bookIssue = bookIssueMapper.toUpdate(requestDto, bookIssue);

        assignBookToStudent(bookIssue, student, libraryBook);
        return bookIssueMapper.toDTO(bookIssueRepository.save(bookIssue));
    }

    @Override
    public BookIssueDTO getBookIssueById(Long id) {
        BookIssue bookIssue = allRepositoryMethods.getBooksIssueById(id);
        return bookIssueMapper.toDTO(bookIssue);
    }

    @Override
    public Page<BookIssueDTO> getActiveIssuedBooks(int pageNo, int pageSize) {

        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;
        Sort sort = Sort.by("issueDate").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<BookIssue> bookIssuePage = bookIssueRepository.findAllByStatus(BookIssueStatus.valueOf(BookIssueStatus.ISSUED.name()), pageable);

        if (bookIssuePage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return bookIssuePage.map(bookIssueMapper::toDTO);
    }

    @Override
    public Page<BookIssueDTO> getReturnedBooks(int pageNo, int pageSize) {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = now.withDayOfYear(1);
        LocalDate endOfYear = now.withMonth(12).withDayOfMonth(31);

        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;
        Sort sort = Sort.by("issueDate").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<BookIssue> bookIssuePage = bookIssueRepository.findAllByStatusAndReturnDateBetween(
                BookIssueStatus.RETURNED, startOfYear, endOfYear, pageable);

        if (bookIssuePage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return bookIssuePage.map(bookIssueMapper::toDTO);
    }

    @Override
    public List<BookIssueDTO> getIssuesByStudentId(Long studentId) {
        if (studentId == null) {
            throw new BadRequestException("Student id cannot be null");
        }

        if (!studentRepository.existsById(studentId)) {
            throw new DataNotFoundException("Student not found with id: " + studentId);
        }

        List<BookIssue> bookIssues = bookIssueRepository.findAllByStudent_IdAndStatus(studentId, BookIssueStatus.ISSUED);

        if (bookIssues.isEmpty()) {
            return List.of();
        }

        return bookIssueMapper.toDTOList(bookIssues);
    }

    @Override
    public List<BookIssueDTO> getIssuesByBookId(Long bookId) {
        if (bookId == null) {
            throw new BadRequestException("Book id cannot be null");
        }

        if (!libraryBookRepository.existsById(bookId)) {
            throw new DataNotFoundException("Book not found with id: " + bookId);
        }

        List<BookIssue> bookIssues = bookIssueRepository.findAllByLibraryBook_Id(bookId);
        if (bookIssues.isEmpty()) {
            return List.of();        }

        return bookIssueMapper.toDTOList(bookIssues);
    }

    @Override
    @Transactional
    public void returnBook(Long studentId, Long bookId) {
        if (studentId == null || bookId == null) {
            throw new BadRequestException("Student id or book id cannot be null");
        }

        BookIssue bookIssue = bookIssueRepository
                .findByStudent_IdAndLibraryBook_IdAndStatus(studentId, bookId, BookIssueStatus.valueOf(BookIssueStatus.ISSUED.name()))
                .orElseThrow(() -> new DataNotFoundException("No active issue found for student id: " + studentId + " and book id: " + bookId));

        returnBookByIssueId(studentId, bookIssue.getId());
    }

    @Override
    public Long countActiveIssuedBooks() {
        return bookIssueRepository.countByStatus(BookIssueStatus.valueOf(BookIssueStatus.ISSUED.name()));
    }

    @Override
    public Boolean isBookCurrentlyIssued(Long bookId) {
        if (bookId == null) {
            throw new BadRequestException("Book id cannot be null");
        }

        return bookIssueRepository.existsByLibraryBook_IdAndStatus(bookId, BookIssueStatus.valueOf(BookIssueStatus.ISSUED.name()));
    }

    @Override
    public Page<BookIssueDTO> getOverdueBooks(int pageNo, int pageSize) {
        LocalDate today = LocalDate.now();
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;
        Sort sort = Sort.by("issueDate").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<BookIssue> overdueBookPage = bookIssueRepository
                .findAllByStatusAndDueDateBefore(BookIssueStatus.valueOf(BookIssueStatus.ISSUED.name()), today, pageable);

        if (overdueBookPage.getContent().isEmpty()) {
            return Page.empty(pageable);        }

        return overdueBookPage.map(bookIssueMapper::toDTO);
    }

    public Page<BookIssueDTO> getAllIssuedBooks(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;
        Sort sort = Sort.by("issueDate").descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<BookIssue> issuePage = bookIssueRepository.findAllByStatus(BookIssueStatus.valueOf(BookIssueStatus.ISSUED.name()), pageable);

        if (issuePage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return issuePage.map(bookIssueMapper::toDTO);
    }

    @Transactional
    public void returnBookByIssueId(Long studentId, Long id) {
        BookIssue issue = allRepositoryMethods.getBooksIssueById(id);
        Student student = allRepositoryMethods.getStudentById(studentId);

        if (issue.getStatus().equals(BookIssueStatus.RETURNED.name())) {
            throw new RuntimeException("Book already returned");
        }

        LocalDate today = LocalDate.now();
        issue.setReturnDate(today);

        if (today.isAfter(issue.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(issue.getDueDate(), today);
            issue.setFineAmount(daysLate * FINE_PER_DAY);
        }

        issue.setStatus(BookIssueStatus.valueOf(BookIssueStatus.RETURNED.name()));

        LibraryBook book = issue.getLibraryBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        student.removeBookIssue(issue);
        book.removeBookIssue(issue);

        libraryBookRepository.save(book);
        bookIssueRepository.save(issue);
    }

    @Transactional
    public BookIssueDTO extendDueDate(Long id, LocalDate newDueDate) {
        if (newDueDate == null) {
            throw new BadRequestException("New due date cannot be null");
        }

        BookIssue issue = allRepositoryMethods.getBooksIssueById(id);

        if (issue.getStatus().equals(BookIssueStatus.RETURNED.name())) {
            throw new RuntimeException("Cannot extend due date for already returned book");
        }

        if (newDueDate.isBefore(issue.getDueDate())) {
            throw new BadRequestException("New due date cannot be earlier than current due date");
        }

        if (newDueDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("New due date cannot be in the past");
        }

        issue.setDueDate(newDueDate);
        BookIssue savedIssue = bookIssueRepository.save(issue);
        return bookIssueMapper.toDTO(savedIssue);
    }

    @Override
    public BookIssueDTO updateBookIssue(Long id, BookIssueRequestDTO requestDto) {
        checkValidation(requestDto.getBookId(), requestDto.getStudentId());

        if(bookIssueRepository.existsByLibraryBook_IdAndStudent_IdAndIdNot(requestDto.getBookId(),
                requestDto.getStudentId(), id)) {
            throw new BadRequestException("Book is already issued, can't update now.");
        }

        Student newStudent = allRepositoryMethods.getStudentById(requestDto.getStudentId());
        LibraryBook newBook = allRepositoryMethods.getLibraryBookById(requestDto.getBookId());
        BookIssue bookIssue = allRepositoryMethods.getBooksIssueById(id);
        assignBookToStudent(bookIssue,newStudent,newBook);

        bookIssue = bookIssueMapper.toUpdate(requestDto, bookIssue);
        BookIssue savedIssue = bookIssueRepository.save(bookIssue);
        return bookIssueMapper.toDTO(savedIssue);
    }

    @Override
    public BookIssueDTO partialUpdateBookIssue(Long id, BookIssueRequestDTO requestDto) {

        BookIssue bookIssue = allRepositoryMethods.getBooksIssueById(id);

        Long bookId = bookIssue.getLibraryBook().getId();
        Long studentId = bookIssue.getStudent().getId();

        if(requestDto.getBookId() != null && !requestDto.getBookId().equals(bookId)) {
            bookId = requestDto.getBookId();
        }

        if(requestDto.getStudentId() != null && !requestDto.getStudentId().equals(studentId)) {
            studentId = requestDto.getStudentId();
        }

        checkValidation(bookId, studentId);

        if(bookIssueRepository.existsByLibraryBook_IdAndStudent_IdAndIdNot(bookId, studentId, id)) {
            throw new BadRequestException("Book is already issued, can't update now.");
        }

        Student newStudent = allRepositoryMethods.getStudentById(studentId);
        LibraryBook newBook = allRepositoryMethods.getLibraryBookById(bookId);

        assignBookToStudent(bookIssue, newStudent, newBook);

        bookIssue = bookIssueMapper.partialUpdate(requestDto, bookIssue);
        BookIssue savedIssue = bookIssueRepository.save(bookIssue);

        return bookIssueMapper.toDTO(savedIssue);
    }
}