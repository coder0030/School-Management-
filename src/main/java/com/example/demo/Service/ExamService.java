package com.example.demo.Service;

import com.example.demo.DTO.ExamDTO;
import com.example.demo.RequestDTO.ExamRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ExamService {
    ExamDTO createExam(@Valid ExamRequestDTO dto);

    ExamDTO getExamById(Long examId);

    ExamDTO updateExam(Long examId, @Valid ExamRequestDTO dto);

    ExamDTO partialUpdateExam(Long examId, ExamRequestDTO dto);

    void deleteExam(Long examId);

    Page<ExamDTO> getAllExams(int pageNo, int pageSize);

    Page<ExamDTO> getExamsByClassId(Long classId, int pageNo, int pageSize);

    Page<ExamDTO> getUpcomingExams(int pageNo, int pageSize);

    Page<ExamDTO> getPastExams(int pageNo, int pageSize);

    Page<ExamDTO> getExamsByStudentId(Long studentId, int pageNo, int pageSize);

    Page<ExamDTO> getExamsBySubjectId(Long subjectId, int pageNo, int pageSize);

    Page<ExamDTO> getExamsByDateRange(String startDate, String endDate, int pageNo, int pageSize);
}
