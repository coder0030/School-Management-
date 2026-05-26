package com.example.demo.ServiceImpl;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.FeeType;
import com.example.demo.Helper.Status;
import com.example.demo.Mapper.AttendanceMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.MyException.DuplicateResourceException;
import com.example.demo.MyException.ResourceNotFoundException;
import com.example.demo.Repository.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllRepositoryMethods {

    private final ClassSubjectRepository classSubjectRepository;
    private final ExamRepository examRepository;
    private final DepartmentRepository departmentRepository;
    private final MarksRepository marksRepository;
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final LibraryBookRepository libraryBookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final TimetableRepository timetableRepository;
    private final ParentRepository parentRepository;
    private final AdminRepository adminRepository;
    private final LibrarianRepository librarianRepository;
    private final AccountantRepository accountantRepository;


    public ClassSubject getClassSubjectById(Long id) {
        log.info("Fetching ClassSubject with id: {}", id);
        return classSubjectRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ClassSubject not found with id: {}", id);
                    return new BadRequestException("ClassSubject not found with id: " + id);
                });
    }
    public Exam getExamById(Long id) {
        log.info("Fetching Exam with id: {}", id);
        return examRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Exam not found with id: {}", id);
                    return new BadRequestException("Exam not found with id: " + id);
                });
    }

    public Department getDepartmentById(Long id) {
        log.info("Fetching Department with id: {}", id);
        return departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department not found with id: {}", id);
                    return new BadRequestException("Department not found with id: " + id);
                });
    }

    public Marks getMarksById(Long id) {
        log.info("Fetching Marks with id: {}", id);
        return marksRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Marks not found with id: {}", id);
                    return new BadRequestException("Marks not found with id: " + id);
                });
    }

    public Teacher getTeacherById(Long id) {
        log.info("Fetching Teacher with id: {}", id);
        return teacherRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Teacher not found with id: {}", id);
                    return new BadRequestException("Teacher not found with id: " + id);
                });
    }

    public Student getStudentById(Long id) {
        log.info("Fetching Student with id: {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Student not found with id: {}", id);
                    return new BadRequestException("Student not found with id: " + id);
                });
    }

    public Attendance getAttendanceById(Long id) {
        log.info("Fetching Attendance with id: {}", id);
        return attendanceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Attendance not found with id: {}", id);
                    return new BadRequestException("Attendance not found with id: " + id);
                });
    }

    public FeeStructure getFeesStructureById(Long id) {
        log.info("Fetching FeesStructure with id: {}", id);
        return feeStructureRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("FeesStructure not found with id: {}", id);
                    return new BadRequestException("FeesStructure not found with id: " + id);
                });
    }

    public void checkFeesStructByTypeAndClassId(FeeType feeType, Long classId) {
        log.info("Fetching FeesStructure with id: {}", classId);

        if (feeStructureRepository.existsByFeeTypeAndClassEntity_Id(feeType, classId)) {
            throw new DuplicateResourceException("FeesStructure with FeeType: " + feeType +
                    ", for class ID: " + classId + " already exists.");
        }
    }

    public LibraryBook getLibraryBookById(Long id) {
        log.info("Fetching LibraryBook with id: {}", id);

        return libraryBookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("LibraryBook with id: {} not found", id);
                    return new ResourceNotFoundException(
                            "LibraryBook not found with id: " + id);
                });
    }

    public void checkLibraryBookByIdAndStudentId(@NotNull(message = "Book id is required") Long bookId, @NotNull(message = "Student id is required") Long studentId) {
        log.info("Fetching LibraryBook with id: {} for student id: {}", bookId, studentId);
        BookIssue bookIssue = bookIssueRepository.findByIdAndStudent_Id(bookId, studentId).orElse(null);

        if (bookIssue != null) {

            log.error("LibraryBook with id: {} for student id: {} already issued", bookId, studentId);

            Student student = bookIssue.getStudent();
            throw new BadRequestException("Book is already registered with student id: " + studentId + " " +
                    "student name : " + student.getFirstName() + ", issuedAt : " + bookIssue.getIssueDate() +
                    " and Return date : " + bookIssue.getDueDate());
        }
    }

    public BookIssue getBooksIssueById(Long id) {
        log.info("Fetching BookIssue with id: {}", id);

        return bookIssueRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("LibraryBook with id: {} not found", id);
                    return new ResourceNotFoundException(
                            "LibraryBook not found with id: " + id);
                });
    }

    public boolean existsTeacherByEmailOrPhone(String email, String phone) {
        return teacherRepository.existsByEmailOrPhone(email,phone);
    }

    public boolean existsStudentByEmailOrPhone(String email, String phone) {
        return studentRepository.existsByEmailOrPhone(email,phone);
    }

    public ClassEntity getClassById(Long classId) {
        return classRepository.findById(classId).orElseThrow(() -> new BadRequestException(
                " class id : " + classId + " not exists."));
    }

    public Subject getSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId).orElseThrow(() -> new BadRequestException(
                " class id : " + subjectId + " not exists."));
    }

    public ClassTeacher getClassTeacherById(Long id) {
        return classTeacherRepository.findById(id).orElseThrow(() -> new BadRequestException(
                " classTeacher id : " + id + " not exists."));
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email).orElseThrow(() -> new BadRequestException(
                "student  with email " + email + "not exists."));
    }

    public Timetable getTimetableById(Long id) {
        return timetableRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Timetable  with id " + id + "not exists."));
    }

    public StudentFee getStudentFeeById(Long id) {
        return studentFeeRepository.findById(id).orElseThrow(() ->
                new BadRequestException("StudentFee  with id " + id + "not exists."));
    }

    public Parent getParentById(Long id) {
        return parentRepository.findById(id).orElseThrow(() -> new BadRequestException("Parent id : " + id +
                " not exists."));
    }

    public Parent getParentByEmail(String email) {
        return parentRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Parent id : " + email +
                " not exists."));
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new BadRequestException("Admin id : " + id +
                " not exists."));
    }

    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Admin id : " + email +
                " not exists."));
    }

    public Librarian getLibrarianById(Long id) {
        return librarianRepository.findById(id).orElseThrow(() -> new BadRequestException("Librarian id : " + id +
                " not exists."));
    }

    public Accountant getAccountantById(Long id) {
        return accountantRepository.findById(id).orElseThrow(() -> new BadRequestException("Accountant id : " + id +
                " not exists."));
    }

    public Librarian getLibrarianByEmail(String email) {
        return librarianRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Librarian email : " + email +
                " not exists."));
    }

    public Accountant getAccountantByEmail(String email) {
        return accountantRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Librarian email : " + email +
                " not exists."));
    }
}