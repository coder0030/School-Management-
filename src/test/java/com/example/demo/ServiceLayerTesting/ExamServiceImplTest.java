package com.example.demo.ServiceLayerTesting;

import com.example.demo.DTO.ExamDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.Exam;
import com.example.demo.Mapper.ExamMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.ExamRepository;
import com.example.demo.RequestDTO.ExamRequestDTO;
import com.example.demo.ServiceImpl.AllRepositoryMethods;
import com.example.demo.ServiceImpl.ExamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private ExamMapper examMapper;

    @Mock
    private AllRepositoryMethods allRepositoryMethods;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExamServiceImpl examService;

    private Exam exam;
    private ExamDTO examDTO;
    private ExamRequestDTO examRequestDTO;
    private ClassEntity classEntity;
    private Long examId;
    private Long classId;
    private String currentYear;

    @BeforeEach
    void setUp() {
        examId = 1L;
        classId = 10L;
        currentYear = String.valueOf(LocalDate.now().getYear());

        classEntity = ClassEntity.builder()
                .id(classId)
                .className("Class 10A")
                .examsList(new ArrayList<>())
                .build();

        exam = Exam.builder()
                .id(examId)
                .examName("Final Examination")
                .examType("THEORY")
                .academicYear(currentYear)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(20))
                .classEntity(classEntity)
                .createdAt(LocalDate.now())
                .build();

        examDTO = ExamDTO.builder()
                .id(examId)
                .examName("Final Examination")
                .examType("THEORY")
                .academicYear(currentYear)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(20))
                .build();

        examRequestDTO = ExamRequestDTO.builder()
                .examName("Final Examination")
                .examType("THEORY")
                .classId(classId)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(20))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Should create exam successfully")
    void createExam_Success() {
        when(allRepositoryMethods.getClassById(classId)).thenReturn(classEntity);
        when(examRepository.existsByExamNameAndExamTypeAndAcademicYearAndClassEntity_Id(
                anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(false);
        when(examMapper.toEntityWithValidation2(any(ExamRequestDTO.class), any(Exam.class)))
                .thenReturn(exam);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        when(examMapper.toDTO(any(Exam.class))).thenReturn(examDTO);

        ExamDTO result = examService.createExam(examRequestDTO);

        assertNotNull(result);
        assertEquals(examDTO.getExamName(), result.getExamName());
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    @Order(2)
    @DisplayName("Should throw exception when exam already exists in current academic year")
    void createExam_ExamAlreadyExists() {
        when(allRepositoryMethods.getClassById(classId)).thenReturn(classEntity);
        when(examRepository.existsByExamNameAndExamTypeAndAcademicYearAndClassEntity_Id(
                anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            examService.createExam(examRequestDTO);
        });
        verify(examRepository, never()).save(any(Exam.class));
    }

    @Test
    @Order(3)
    @DisplayName("Should throw exception when class not found while creating exam")
    void createExam_ClassNotFound() {
        when(allRepositoryMethods.getClassById(classId))
                .thenThrow(new BadRequestException("Class not found"));

        assertThrows(BadRequestException.class, () -> {
            examService.createExam(examRequestDTO);
        });
        verify(examRepository, never()).save(any(Exam.class));
    }

    @Test
    @Order(4)
    @DisplayName("Should get all exams successfully")
    void getAllExams_Success() {
        List<Exam> exams = Arrays.asList(exam);
        List<ExamDTO> examDTOs = Arrays.asList(examDTO);

        when(examRepository.findAll()).thenReturn(exams);
        when(examMapper.toDTOList(exams)).thenReturn(examDTOs);

        List<ExamDTO> result = examService.getAllExams();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(examRepository, times(1)).findAll();
    }

    @Test
    @Order(5)
    @DisplayName("Should throw exception when no exams found")
    void getAllExams_NoExamsFound() {
        when(examRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(DataNotFoundException.class, () -> {
            examService.getAllExams();
        });
    }

    @Test
    @Order(6)
    @DisplayName("Should get exam by ID successfully")
    void getExamById_Success() {
        when(allRepositoryMethods.getExamById(examId)).thenReturn(exam);
        when(examMapper.toDTO(exam)).thenReturn(examDTO);

        ExamDTO result = examService.getExamById(examId);

        assertNotNull(result);
        assertEquals(examId, result.getId());
        verify(allRepositoryMethods, times(1)).getExamById(examId);
    }

    @Test
    @Order(7)
    @DisplayName("Should update exam successfully without changing class")
    void updateExam_Success_NoClassChange() {
        when(allRepositoryMethods.getExamById(examId)).thenReturn(exam);
        when(examMapper.toEntityWithValidation2(any(ExamRequestDTO.class), any(Exam.class)))
                .thenReturn(exam);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        when(examMapper.toDTO(any(Exam.class))).thenReturn(examDTO);

        ExamDTO result = examService.updateExam(examId, examRequestDTO);

        assertNotNull(result);
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    @Order(8)
    @DisplayName("Should update exam successfully when changing class")
    void updateExam_Success_WithClassChange() {
        ClassEntity newClassEntity = ClassEntity.builder()
                .id(20L)
                .className("Class 10B")
                .examsList(new ArrayList<>())
                .build();

        examRequestDTO.setClassId(20L);

        when(allRepositoryMethods.getExamById(examId)).thenReturn(exam);
        when(allRepositoryMethods.getClassById(20L)).thenReturn(newClassEntity);

        when(examRepository.existsByExamNameAndExamTypeAndAcademicYearAndClassEntity_Id(
                anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(false);

        when(examMapper.toEntityWithValidation2(any(ExamRequestDTO.class), any(Exam.class)))
                .thenReturn(exam);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        when(examMapper.toDTO(any(Exam.class))).thenReturn(examDTO);

        ExamDTO result = examService.updateExam(examId, examRequestDTO);

        assertNotNull(result);
        verify(examRepository, times(1)).save(any(Exam.class));
    }


    @Test
    @Order(9)
    @DisplayName("Should delete exam successfully")
    void deleteExam_Success() {
        when(allRepositoryMethods.getExamById(examId)).thenReturn(exam);
        doNothing().when(examRepository).delete(any(Exam.class));

        assertDoesNotThrow(() -> examService.deleteExam(examId));
        verify(examRepository, times(1)).delete(exam);
    }

    @Test
    @Order(10)
    @DisplayName("Should get exams by class ID with pagination")
    void getExamsByClassId_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<Exam> exams = Arrays.asList(exam);
        Page<Exam> examPage = new PageImpl<>(exams, pageable, 1);

        when(examRepository.findByClassEntity_Id(eq(classId), any(Pageable.class)))
                .thenReturn(examPage);
        when(examMapper.toDTO(any(Exam.class))).thenReturn(examDTO);

        Page<ExamDTO> result = examService.getExamsByClassId(classId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(examRepository, times(1)).findByClassEntity_Id(eq(classId), any(Pageable.class));
    }

    @Test
    @Order(11)
    @DisplayName("Should get upcoming exams successfully")
    void getUpcomingExams_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<Exam> exams = Arrays.asList(exam);
        Page<Exam> examPage = new PageImpl<>(exams, pageable, 1);
        LocalDate today = LocalDate.now();

        when(examRepository.findByStartDateGreaterThanEqual(eq(today), any(Pageable.class)))
                .thenReturn(examPage);
        when(examMapper.toDTO(any(Exam.class))).thenReturn(examDTO);

        Page<ExamDTO> result = examService.getUpcomingExams(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(examRepository, times(1)).findByStartDateGreaterThanEqual(eq(today), any(Pageable.class));
    }
}