package com.example.demo.Service;

import com.example.demo.DTO.SubjectDTO;
import com.example.demo.RequestDTO.SubjectRequestDTO;

import java.util.List;

public interface SubjectService {
    SubjectDTO createSubject(SubjectRequestDTO subjectRequestDTO);

    List<SubjectDTO> getAllSubjects();

    SubjectDTO getSubjectById(Long id);

    SubjectDTO updateSubjectById(Long id, SubjectRequestDTO subjectRequestDTO);

    SubjectDTO partialUpdateSubjectById(Long id, SubjectRequestDTO subjectRequestDTO);

    String deleteSubjectById(Long id);
}
