package com.example.demo.Service;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.RequestDTO.MarksRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MarksService {

    MarksDTO createMarks(MarksRequestDTO requestDTO);

    List<MarksDTO> createMarksInBulk(List<MarksRequestDTO> marksList);

    void deleteMarks(Long id);

    MarksDTO updateMarks(Long id, MarksRequestDTO requestDTO);

    MarksDTO partialUpdateMarks(Long id, MarksRequestDTO requestDTO);

    MarksDTO getMarksByStudentAndSubject(Long studentId, Long subjectId);

    Double getAverageMarksForStudent(Long studentId);

    Double getAverageMarksForSubject(Long subjectId);

    List<MarksDTO> getTopPerformingStudentsBySubject(Long subjectId, int limit);

    MarksDTO getMarksById(Long id);

    Page<MarksDTO> getAllMarks(int pageNo, int pageSize);

    Page<MarksDTO> getMarksBySubject(Long subjectId, int pageNo, int pageSize);

    Page<MarksDTO> getMarksByStudentAndExam(Long studentId, Long examId, int pageNo, int pageSize);

    Page<MarksDTO> getMarksByExam(Long examId, int pageNo, int pageSize);
}