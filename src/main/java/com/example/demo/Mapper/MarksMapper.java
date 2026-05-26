package com.example.demo.Mapper;

import com.example.demo.DTO.MarksDTO;
import com.example.demo.ENTITY.Marks;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.RequestDTO.MarksRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarksMapper {
    public MarksDTO toDTO(Marks marks) {
        if(marks == null) return null;

        return MarksDTO.builder()
                .id(marks.getId())
                .studentId(marks.getStudent().getId())
                .subjectId(marks.getSubject().getId())
                .examId(marks.getExam().getId())
                .marksObtained(marks.getMarksObtained())
                .grade(marks.getGrade())
                .build();
    }

    public Marks toEntity(MarksRequestDTO marksRequestDTO, Marks marks, boolean partialUpdate) {

        if(!partialUpdate && ( marksRequestDTO.getMarksObtained() == null ||
        marksRequestDTO.getGrade() == null)) {
            throw new IncompleteDataException("Data is incompleted, Request Body is not fully filled");
        }

        if(marksRequestDTO.getMarksObtained() != null ) {
            marks.setMarksObtained(marksRequestDTO.getMarksObtained());
        }
        if(marksRequestDTO.getGrade() != null) {
            marks.setGrade(marksRequestDTO.getGrade());
        }

        return marks;
    }

    public List<MarksDTO> toDTOList(List<Marks> marks) {
        if(marks.isEmpty()) return null;

        return marks.stream().map(this::toDTO).toList();
    }
}
