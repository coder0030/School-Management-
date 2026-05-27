package com.example.demo.ServiceImpl;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.ENTITY.Exam;
import com.example.demo.ENTITY.Marks;
import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.Subject;
import com.example.demo.Mapper.MarksMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.ExamRepository;
import com.example.demo.Repository.MarksRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.SubjectRepository;
import com.example.demo.RequestDTO.MarksRequestDTO;
import com.example.demo.Service.MarksService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarksServiceImpl implements MarksService {

    private final AllRepositoryMethods allRepositoryMethods;
    private final MarksRepository marksRepository;
    private final MarksMapper marksMapper;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final ModelMapper modelMapper;

    private void checkValidations(Long studentId, Long subjectId, Long examId) {
        if (studentId == null || subjectId == null || examId == null) {
            throw new BadRequestException("Student id, exam id and subject id can't be null");
        }

        if (!studentRepository.existsById(studentId)) {
            throw new BadRequestException("Student id: " + studentId + " does not exist");
        }

        if (!subjectRepository.existsById(subjectId)) {
            throw new BadRequestException("Subject id: " + subjectId + " does not exist");
        }

        if (!examRepository.existsById(examId)) {
            throw new BadRequestException("Exam id: " + examId + " does not exist");
        }
    }

    private boolean checkDuplicate(Long studentId, Long subjectId, Long examId) {
        return marksRepository.existsByStudent_IdAndSubject_IdAndExam_Id(studentId, subjectId, examId);
    }

    private void addRelation(Marks marks, Student student, Subject subject, Exam exam) {
        student.addMarks(marks);
        subject.addMarks(marks);
        exam.addMarks(marks);

        marks.setExam(exam);
        marks.setSubject(subject);
        marks.setStudent(student);
    }

    private void removeRelation(Marks marks, Student student, Subject subject, Exam exam) {
        if (student != null) student.removeMarks(marks);
        if (subject != null) subject.removeMarks(marks);
        if (exam != null) exam.removeMarks(marks);
    }

    @Override
    public MarksDTO createMarks(MarksRequestDTO requestDTO) {
        checkValidations(requestDTO.getStudentId(), requestDTO.getSubjectId(), requestDTO.getExamId());

        Student student = allRepositoryMethods.getStudentById(requestDTO.getStudentId());
        Subject subject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        Exam exam = allRepositoryMethods.getExamById(requestDTO.getExamId());

        if (checkDuplicate(student.getId(), subject.getId(), exam.getId())) {
            throw new BadRequestException("Marks already have been created for student id: " + student.getId() +
                    " subject id: " + subject.getId() + " exam id: " + exam.getId());
        }

        Marks marks = new Marks();
        marks = marksMapper.toEntity(requestDTO, marks, false);
        addRelation(marks, student, subject, exam);

        Marks created = marksRepository.save(marks);
        return marksMapper.toDTO(created);
    }

    @Override
    public List<MarksDTO> createMarksInBulk(List<MarksRequestDTO> marksList) {
        if (marksList == null || marksList.isEmpty()) {
            throw new BadRequestException("Marks list cannot be empty");
        }

        List<Marks> savedMarks = new ArrayList<>();

        for (MarksRequestDTO requestDTO : marksList) {
            checkValidations(requestDTO.getStudentId(), requestDTO.getSubjectId(), requestDTO.getExamId());

            Student student = allRepositoryMethods.getStudentById(requestDTO.getStudentId());
            Subject subject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
            Exam exam = allRepositoryMethods.getExamById(requestDTO.getExamId());

            if (!checkDuplicate(student.getId(), subject.getId(), exam.getId())) {
                Marks marks = new Marks();
                marks = marksMapper.toEntity(requestDTO, marks, false);
                addRelation(marks, student, subject, exam);
                savedMarks.add(marksRepository.save(marks));
            }
        }

        if (savedMarks.isEmpty()) {
            return List.of();
        }

        return marksMapper.toDTOList(savedMarks);
    }

    @Override
    public void deleteMarks(Long id) {
        Marks marks = allRepositoryMethods.getMarksById(id);
        removeRelation(marks, marks.getStudent(), marks.getSubject(), marks.getExam());
        marksRepository.delete(marks);
    }

    @Override
    public MarksDTO updateMarks(Long id, MarksRequestDTO requestDTO) {
        Marks marks = allRepositoryMethods.getMarksById(id);

        checkValidations(requestDTO.getStudentId(), requestDTO.getSubjectId(), requestDTO.getExamId());

        boolean duplicateExists = marksRepository.existsByStudent_IdAndSubject_IdAndExam_Id(
                requestDTO.getStudentId(), requestDTO.getSubjectId(), requestDTO.getExamId()
        );

        if (duplicateExists &&
                (!marks.getStudent().getId().equals(requestDTO.getStudentId()) ||
                        !marks.getSubject().getId().equals(requestDTO.getSubjectId()) ||
                        !marks.getExam().getId().equals(requestDTO.getExamId()))) {
            throw new BadRequestException("Marks already exist for this student, subject, and exam combination");
        }

        Student oldStudent = marks.getStudent();
        Subject oldSubject = marks.getSubject();
        Exam oldExam = marks.getExam();

        Student newStudent = allRepositoryMethods.getStudentById(requestDTO.getStudentId());
        Subject newSubject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        Exam newExam = allRepositoryMethods.getExamById(requestDTO.getExamId());

        removeRelation(marks, oldStudent, oldSubject, oldExam);

        marks = marksMapper.toEntity(requestDTO, marks, false);

        addRelation(marks, newStudent, newSubject, newExam);

        Marks updated = marksRepository.save(marks);
        return marksMapper.toDTO(updated);
    }

    @Override
    public MarksDTO partialUpdateMarks(Long id, MarksRequestDTO requestDTO) {

        Marks marks = allRepositoryMethods.getMarksById(id);

        Student currentStudent = marks.getStudent();
        Subject currentSubject = marks.getSubject();
        Exam currentExam = marks.getExam();

        Student finalStudent = currentStudent;
        Subject finalSubject = currentSubject;
        Exam finalExam = currentExam;

        if (requestDTO.getStudentId() != null &&
                !requestDTO.getStudentId().equals(currentStudent.getId())) {
            finalStudent = allRepositoryMethods.getStudentById(requestDTO.getStudentId());
        }

        if (requestDTO.getSubjectId() != null &&
                !requestDTO.getSubjectId().equals(currentSubject.getId())) {
            finalSubject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        }

        if (requestDTO.getExamId() != null &&
                !requestDTO.getExamId().equals(currentExam.getId())) {
            finalExam = allRepositoryMethods.getExamById(requestDTO.getExamId());
        }

        boolean duplicateExists =
                marksRepository.existsByStudent_IdAndSubject_IdAndExam_IdAndIdNot(
                        finalStudent.getId(),
                        finalSubject.getId(),
                        finalExam.getId(),
                        id
                );

        if (duplicateExists) {
            throw new BadRequestException(
                    "Marks already exist for this student, subject, and exam combination");
        }

        if (!currentStudent.equals(finalStudent)) {
            currentStudent.removeMarks(marks);
            finalStudent.addMarks(marks);
            marks.setStudent(finalStudent);
        }

        if (!currentSubject.equals(finalSubject)) {
            currentSubject.removeMarks(marks);
            finalSubject.addMarks(marks);
            marks.setSubject(finalSubject);
        }

        if (!currentExam.equals(finalExam)) {
            currentExam.removeMarks(marks);
            finalExam.addMarks(marks);
            marks.setExam(finalExam);
        }

        marks = marksMapper.toEntity(requestDTO, marks, true);

        Marks updated = marksRepository.save(marks);

        return marksMapper.toDTO(updated);
    }

    @Override
    public Page<MarksDTO> getAllMarks(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("grade").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Marks> marksPage = marksRepository.findAll(pageable);

        if (marksPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }
        return marksPage.map(marksMapper::toDTO);
    }

    @Override
    public MarksDTO getMarksByStudentAndSubject(Long studentId, Long subjectId) {
        if (studentId == null || subjectId == null) {
            throw new BadRequestException("Student ID and Subject ID cannot be null");
        }

        Marks marks = marksRepository.findByStudent_IdAndSubject_Id(studentId, subjectId)
                .orElseThrow(() -> new DataNotFoundException(
                        "No marks found for student ID: " + studentId + " and subject ID: " + subjectId
                ));

        return marksMapper.toDTO(marks);
    }

    @Override
    public Page<MarksDTO> getMarksBySubject(Long subjectId, int pageNo, int pageSize) {
        if (subjectId == null) {
            throw new BadRequestException("Subject ID cannot be null");
        }

        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


        Page<Marks> marksPage = marksRepository.findBySubject_Id(subjectId, pageable);

        if (marksPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return marksPage.map(marksMapper::toDTO);
    }

    @Override
    public Page<MarksDTO> getMarksByStudentAndExam(Long studentId, Long examId, int pageNo, int pageSize) {
        if (studentId == null || examId == null) {
            throw new BadRequestException("Student ID and Exam ID cannot be null");
        }

        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


        Page<Marks> marksPage = marksRepository.findByStudent_IdAndExam_Id(studentId, examId, pageable);

        if (marksPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return marksPage.map(marksMapper::toDTO);
    }

    @Override
    public Double getAverageMarksForStudent(Long studentId) {
        if (studentId == null) {
            throw new BadRequestException("Student ID cannot be null");
        }

        Student student = allRepositoryMethods.getStudentById(studentId);
        List<Marks> marks = student.getMarksList();

        if (marks == null || marks.isEmpty()) {
            return 0.0;
        }

        double total = marks.stream()
                .mapToDouble(m -> m.getMarksObtained() != null ? m.getMarksObtained() : 0.0)
                .sum();

        return total / marks.size();
    }

    @Override
    public Double getAverageMarksForSubject(Long subjectId) {
        if (subjectId == null) {
            throw new BadRequestException("Subject ID cannot be null");
        }

        Subject subject = allRepositoryMethods.getSubjectById(subjectId);
        List<Marks> marks = subject.getMarksList();

        if (marks == null || marks.isEmpty()) {
            return 0.0;
        }

        double total = marks.stream()
                .mapToDouble(m -> m.getMarksObtained() != null ? m.getMarksObtained() : 0.0)
                .sum();

        return total / marks.size();
    }

    @Override
    public Page<MarksDTO> getMarksByExam(Long examId, int pageNo, int pageSize) {
        if (examId == null) {
            throw new BadRequestException("Exam ID cannot be null");
        }

        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


        Page<Marks> marksPage = marksRepository.findByExam_Id(examId, pageable);

        if (marksPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return marksPage.map(marksMapper::toDTO);
    }

    @Override
    public List<MarksDTO> getTopPerformingStudentsBySubject(Long subjectId, int limit) {
        if (subjectId == null) {
            throw new BadRequestException("Subject ID cannot be null");
        }

        List<Marks> marks = marksRepository.findTopBySubject_IdOrderByMarksObtainedDesc(subjectId);
        if (marks == null || marks.isEmpty()) {
            return List.of();
        }

        return marksMapper.toDTOList(marks);
    }

    @Override
    public MarksDTO getMarksById(Long id) {
        Marks marks = allRepositoryMethods.getMarksById(id);
        return marksMapper.toDTO(marks);
    }
}