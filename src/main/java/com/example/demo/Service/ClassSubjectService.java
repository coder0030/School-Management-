package com.example.demo.Service;

import com.example.demo.DTO.ClassSubjectDTO;
import com.example.demo.RequestDTO.ClassSubjectRequestDTO;
import com.example.demo.RequestDTO.TeacherSubjectRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface ClassSubjectService {

    ClassSubjectDTO assignSubjectToClass(@Valid ClassSubjectRequestDTO requestDto);

    ClassSubjectDTO getClassSubjectById(Long id);

    void deleteClassSubject(Long id);

    void removeSubjectFromClassById(Long classSubjectId);

    void removeSubjectFromClass(Long classId, Long subjectId);

    List<ClassSubjectDTO> getClassSubjectsBySubjectId(Long subjectId);

    boolean isSubjectAssignedToClass(Long classId, Long subjectId);

    ClassSubjectDTO assignOrUpdateSubjectToClass(Long id, @Valid ClassSubjectRequestDTO requestDto);

    List<ClassSubjectDTO> bulkAssignSubjectsToClass(Long classId, List<TeacherSubjectRequestDTO> requestDTOS);

    void bulkRemoveSubjectsFromClass(Long classId, List<TeacherSubjectRequestDTO> requestDTOS);

    Map<String, Object> getClassSubjectsByClassIdPaged(Long classId, int page, int size);

    ClassSubjectDTO removeClassSubjectToClass(Long classSubjectId, @Valid ClassSubjectRequestDTO requestDTO);

    Page<ClassSubjectDTO> getAllClassSubjects(int pageNo, int pageSize);

    ClassSubjectDTO partialUpdateClassSubject(Long id, @Valid ClassSubjectRequestDTO requestDto);
}