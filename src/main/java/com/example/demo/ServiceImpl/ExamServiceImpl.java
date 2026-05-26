package com.example.demo.ServiceImpl;

import com.example.demo.DTO.ExamDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.Exam;
import com.example.demo.Mapper.ExamMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.ExamRepository;
import com.example.demo.RequestDTO.ExamRequestDTO;
import com.example.demo.Service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final AllRepositoryMethods allRepositoryMethods;
    private final ModelMapper modelMapper;

    private void examValidation(String examName, String examType, Long classId) {

        if (examName == null || examType == null) {
            throw new BadRequestException("Exam name or exam type can't be null.");
        }

        String currYear = String.valueOf(LocalDate.now().getYear());

        if (examRepository.existsByExamNameAndExamTypeAndAcademicYearAndClassEntity_Id(
                examName, examType, currYear, classId)) {

            throw new BadRequestException(
                    "Exam already exists for this class in current academic year."
            );
        }
    }

    @Override
    public ExamDTO createExam(@Valid ExamRequestDTO dto) {

        ClassEntity classEntity = allRepositoryMethods.getClassById(dto.getClassId());

        examValidation(dto.getExamName(), dto.getExamType(), classEntity.getId());

        Exam exam = new Exam();
        exam = examMapper.toEntityWithValidation2(dto, exam);
        exam.setClassEntity(classEntity);
        Exam created = examRepository.save(exam);
        return examMapper.toDTO(created);
    }

    public List<ExamDTO> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        if(exams.isEmpty()) {
            throw new DataNotFoundException("No exam found.");
        }
        return examMapper.toDTOList(exams);
    }

    public ExamDTO getExamById(Long id) {
        Exam exam = allRepositoryMethods.getExamById(id);
        return examMapper.toDTO(exam);
    }

    @Override
    public ExamDTO updateExam(Long id, @Valid ExamRequestDTO dto) {

        Exam exam = allRepositoryMethods.getExamById(id);

        if (dto.getClassId() != null &&
                !dto.getClassId().equals(exam.getClassEntity().getId())) {

            ClassEntity oldClass = exam.getClassEntity();
            ClassEntity newClass = allRepositoryMethods.getClassById(dto.getClassId());

            if (oldClass != null) oldClass.removeExam(exam);
            newClass.addExam(exam);
        }

        if (!exam.getExamName().equals(dto.getExamName()) ||
                !exam.getExamType().equals(dto.getExamType()) ||
                dto.getClassId() != null) {

            examValidation(dto.getExamName(), dto.getExamType(),
                    exam.getClassEntity().getId());
        }

        exam = examMapper.toEntityWithValidation2(dto, exam);

        if (exam.getStartDate() != null && exam.getEndDate() != null &&
                exam.getEndDate().isBefore(exam.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date.");
        }

        Exam updated = examRepository.save(exam);
        return examMapper.toDTO(updated);
    }

    @Override
    public ExamDTO partialUpdateExam(Long id, ExamRequestDTO dto) {

        Exam exam = allRepositoryMethods.getExamById(id);

        if (dto.getClassId() != null &&
                !dto.getClassId().equals(exam.getClassEntity().getId())) {

            ClassEntity oldClass = exam.getClassEntity();
            ClassEntity newClass = allRepositoryMethods.getClassById(dto.getClassId());

            if (oldClass != null) oldClass.removeExam(exam);
            newClass.addExam(exam);
        }

        String finalExamName = dto.getExamName() != null ? dto.getExamName() : exam.getExamName();
        String finalExamType = dto.getExamType() != null ? dto.getExamType() : exam.getExamType();
        Long finalClassId = exam.getClassEntity().getId();

        if (!finalExamName.equals(exam.getExamName()) ||
                !finalExamType.equals(exam.getExamType()) ||
                dto.getClassId() != null) {

            examValidation(finalExamName, finalExamType, finalClassId);
        }

        exam = examMapper.toEntity(dto, exam);
        Exam updated = examRepository.save(exam);
        return examMapper.toDTO(updated);
    }

    public void deleteExam(Long id) {
        Exam exam = allRepositoryMethods.getExamById(id);
        examRepository.delete(exam);
    }

    @Override
    public Page<ExamDTO> getAllExams(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Exam> examPage = examRepository.findAll(pageable);

        if (examPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return examPage.map(examMapper::toDTO);
    }

    @Override
    public Page<ExamDTO> getExamsByClassId(Long classId, int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Exam> examPage = examRepository.findByClassEntity_Id(classId, pageable);

        if (examPage.getContent().isEmpty()) {
            return Page.empty(pageable);        }

        return examPage.map(examMapper::toDTO);
    }

    @Override
    public Page<ExamDTO> getUpcomingExams(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        LocalDate today = LocalDate.now();

        Page<Exam> examPage = examRepository
                .findByStartDateGreaterThanEqual(today, pageable);

        if (examPage.getContent().isEmpty()) {
            return Page.empty(pageable);        }

        return examPage.map(examMapper::toDTO);
    }

    @Override
    public Page<ExamDTO> getPastExams(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        LocalDate today = LocalDate.now();

        Page<Exam> examPage = examRepository
                .findByEndDateBefore(today, pageable);

        if (examPage.getContent().isEmpty()) {
            return Page.empty(pageable);        }

        return examPage.map(examMapper::toDTO);
    }

    @Override
    public Page<ExamDTO> getExamsByStudentId(Long studentId, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public Page<ExamDTO> getExamsBySubjectId(Long subjectId, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public Page<ExamDTO> getExamsByDateRange(String startDate, String endDate, int pageNo, int pageSize) {
        return null;
    }
}