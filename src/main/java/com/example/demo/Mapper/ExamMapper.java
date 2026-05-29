package com.example.demo.Mapper;

import com.example.demo.DTO.ExamDTO;
import com.example.demo.ENTITY.Exam;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.RequestDTO.ExamRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExamMapper {

    public Exam toEntity(ExamRequestDTO examRequestDTO, Exam exam) {
        if (examRequestDTO == null) {
            return null;
        }


        if (examRequestDTO.getExamName() != null) {
            exam.setExamName(examRequestDTO.getExamName());
        }
        if (examRequestDTO.getExamType() != null) {
            exam.setExamType(examRequestDTO.getExamType());
        }
        if (examRequestDTO.getAcademicYear() != null) {
            exam.setAcademicYear(examRequestDTO.getAcademicYear());
        }
        if (examRequestDTO.getSemester() > 0 && examRequestDTO.getSemester() <=
                examRequestDTO.getMaxMarks()) {
            exam.setSemester(examRequestDTO.getSemester());
        }
        if (!examRequestDTO.getStartDate().toString().isBlank()) {
            exam.setStartDate(examRequestDTO.getStartDate());
        }
        if (examRequestDTO.getEndDate() != null) {
            exam.setEndDate(examRequestDTO.getEndDate());
        }
        if (!String.valueOf(examRequestDTO.getMaxMarks()).isBlank()) {
            exam.setMaxMarks(examRequestDTO.getMaxMarks());
        }
        if (!String.valueOf(examRequestDTO.getPassingMarks()).isBlank()) {
            exam.setPassingMarks(examRequestDTO.getPassingMarks());
        }

        return exam;
    }

    public Exam toEntityWithValidation(ExamRequestDTO examRequestDTO) {
        if (examRequestDTO == null) {
            throw new IncompleteDataException("Exam request data cannot be null");
        }

        Exam.ExamBuilder builder = Exam.builder();
        StringBuilder missingFields = new StringBuilder();

        if (examRequestDTO.getExamName() == null || examRequestDTO.getExamName().isBlank()) {
            missingFields.append("Exam name, ");
        } else {
            builder.examName(examRequestDTO.getExamName());
        }

        if (examRequestDTO.getExamType() == null || examRequestDTO.getExamType().isBlank()) {
            missingFields.append("Exam type, ");
        } else {
            builder.examType(examRequestDTO.getExamType());
        }

        if (examRequestDTO.getAcademicYear() == null || examRequestDTO.getAcademicYear().isBlank()) {
            missingFields.append("Academic year, ");
        } else {
            builder.academicYear(examRequestDTO.getAcademicYear());
        }

        if (examRequestDTO.getSemester() <= 0) {
            missingFields.append("Semester, ");
        } else {
            if (examRequestDTO.getSemester() > 8) {
                throw new IncompleteDataException("Semester cannot be greater than 8");
            }
            builder.semester(examRequestDTO.getSemester());
        }

        if (examRequestDTO.getStartDate() == null) {
            missingFields.append("Start date, ");
        } else {
            builder.startDate(examRequestDTO.getStartDate());
        }

        if (examRequestDTO.getEndDate() == null) {
            missingFields.append("End date, ");
        } else {
            builder.endDate(examRequestDTO.getEndDate());
        }

        if (examRequestDTO.getMaxMarks() <= 0) {
            missingFields.append("Max marks, ");
        } else {
            builder.maxMarks(examRequestDTO.getMaxMarks());
        }

        if (examRequestDTO.getPassingMarks() <= 0) {
            missingFields.append("Passing marks, ");
        } else {
            if (examRequestDTO.getPassingMarks() > examRequestDTO.getMaxMarks()) {
                throw new IncompleteDataException("Passing marks cannot be greater than max marks");
            }
            builder.passingMarks(examRequestDTO.getPassingMarks());
        }

        if (examRequestDTO.getStartDate() != null && examRequestDTO.getEndDate() != null) {
            if (examRequestDTO.getStartDate().isAfter(examRequestDTO.getEndDate())) {
                throw new IncompleteDataException("Start date cannot be after end date");
            }
        }

        if (!missingFields.isEmpty()) {
            String missing = missingFields.substring(0, missingFields.length() - 2);
            throw new IncompleteDataException("Missing required fields: " + missing);
        }

        return builder.build();
    }

    public Exam toEntityWithValidation2(ExamRequestDTO examRequestDTO, Exam exam) {
        if (examRequestDTO == null) {
            throw new IncompleteDataException("ExamRequestDTO cannot be null");
        }

        if (examRequestDTO.getExamName() == null || examRequestDTO.getExamName().isBlank()) {
            throw new IncompleteDataException("Exam name is required");
        }
        if (examRequestDTO.getExamType() == null || examRequestDTO.getExamType().isBlank()) {
            throw new IncompleteDataException("Exam type is required");
        }
        if (examRequestDTO.getAcademicYear() == null || examRequestDTO.getAcademicYear().isBlank()) {
            throw new IncompleteDataException("Academic year is required");
        }
        if (examRequestDTO.getSemester() <= 0) {
            throw new IncompleteDataException("Semester must be greater than 0");
        }
        if (examRequestDTO.getStartDate() == null) {
            throw new IncompleteDataException("Start date is required");
        }
        if (examRequestDTO.getEndDate() == null) {
            throw new IncompleteDataException("End date is required");
        }
        if (examRequestDTO.getMaxMarks() <= 0) {
            throw new IncompleteDataException("Max marks must be greater than 0");
        }
        if (examRequestDTO.getPassingMarks() <= 0) {
            throw new IncompleteDataException("Passing marks must be greater than 0");
        }

        if (examRequestDTO.getPassingMarks() > examRequestDTO.getMaxMarks()) {
            throw new IncompleteDataException("Passing marks cannot be greater than max marks");
        }
        if (examRequestDTO.getStartDate().isAfter(examRequestDTO.getEndDate())) {
            throw new IncompleteDataException("Start date cannot be after end date");
        }
        if (examRequestDTO.getSemester() > 8) {
            throw new IncompleteDataException("Semester cannot be greater than 8");
        }


        return toEntity(examRequestDTO, exam);
    }


    public ExamDTO toDTO(Exam exam) {
        if (exam == null) return null;

        return ExamDTO.builder()
                .id(exam.getId())
                .examName(exam.getExamName())
                .examType(exam.getExamType())
                .academicYear(exam.getAcademicYear())
                .semester(exam.getSemester())
                .startDate(exam.getStartDate())
                .endDate(exam.getEndDate())
                .maxMarks(exam.getMaxMarks())
                .passingMarks(exam.getPassingMarks())
                .build();
    }

    public List<ExamDTO> toDTOList(List<Exam> examEntities) {
        if (examEntities == null || examEntities.isEmpty()) return null;

        return examEntities.stream().map(this::toDTO).toList();
    }
}
