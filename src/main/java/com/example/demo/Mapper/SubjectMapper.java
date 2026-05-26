package com.example.demo.Mapper;

import com.example.demo.DTO.SubjectDTO;
import com.example.demo.ENTITY.Subject;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.Repository.SubjectRepository;
import com.example.demo.RequestDTO.SubjectRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubjectMapper {

    private final SubjectRepository subjectRepository;

    public SubjectDTO toDTO(Subject subject) {
        if (subject == null) {
            return null;
        }
        return SubjectDTO.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .credits(subject.getCredits())
                .theoryMarks(subject.getTheoryMarks())
                .practicalMarks(subject.getPracticalMarks())
                .build();
    }

    public Subject toEntity(SubjectRequestDTO subjectRequestDTO, Subject subject) {
        if (subjectRequestDTO == null) {
            return null;
        }

        if (subjectRequestDTO.getSubjectCode() != null) {
            subject.setSubjectCode(subjectRequestDTO.getSubjectCode());
        }
        if (subjectRequestDTO.getSubjectName() != null) {
            subject.setSubjectName(subjectRequestDTO.getSubjectName());
        }
        if (subjectRequestDTO.getCredits() != null) {
            subject.setCredits(subjectRequestDTO.getCredits());
        }
        if (subjectRequestDTO.getTheoryMarks() != null) {
            subject.setTheoryMarks(subjectRequestDTO.getTheoryMarks());
        }
        if (subjectRequestDTO.getPracticalMarks() != null) {
            subject.setPracticalMarks(subjectRequestDTO.getPracticalMarks());
        }

        return subject;
    }

    public List<SubjectDTO> toDTOList(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return null;
        }
        return subjects.stream().map(this::toDTO).toList();
    }

    public Subject requestToEntity(SubjectRequestDTO subjectRequestDTO, Subject subject) {
        boolean valNull = false;

        if (subjectRequestDTO.getSubjectCode() == null) valNull = true;
        if (subjectRequestDTO.getSubjectName() == null) valNull = true;
        if (subjectRequestDTO.getCredits() == null) valNull = true;
        if (subjectRequestDTO.getTheoryMarks() == null) valNull = true;
        if (subjectRequestDTO.getPracticalMarks() == null) valNull = true;

        if (valNull) {
            throw new IncompleteDataException("Update can't be done, data incomplete.");
        }

        subject.setSubjectCode(subjectRequestDTO.getSubjectCode());
        subject.setSubjectName(subjectRequestDTO.getSubjectName());
        subject.setCredits(subjectRequestDTO.getCredits());
        subject.setTheoryMarks(subjectRequestDTO.getTheoryMarks());
        subject.setPracticalMarks(subjectRequestDTO.getPracticalMarks());

        return subject;
    }
}