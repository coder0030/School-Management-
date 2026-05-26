package com.example.demo.Mapper;

import com.example.demo.DTO.StudentFeeResponseDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.FeeStructure;
import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.StudentFee;
import com.example.demo.Helper.StudentFeeStatus;
import com.example.demo.RequestDTO.StudentFeeRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentFeeMapper {

    public StudentFeeResponseDTO toDTO(StudentFee studentFee) {
        if (studentFee == null) {
            return null;
        }

        StudentFeeResponseDTO.StudentFeeResponseDTOBuilder builder = StudentFeeResponseDTO.builder()
                .id(studentFee.getId())
                .totalAmount(studentFee.getTotalAmount())
                .paidAmount(studentFee.getPaidAmount())
                .remainingAmount(studentFee.getRemainingAmount())
                .dueDate(studentFee.getDueDate())
                .status(studentFee.getStatus());

        if (studentFee.getStudent() != null) {
            builder.studentId(studentFee.getStudent().getId())
                    .studentName(studentFee.getStudent().getStudentName());
        }

        if (studentFee.getClassEntity() != null) {
            builder.classEntityId(studentFee.getClassEntity().getId())
                    .className(studentFee.getClassEntity().getClassName());
        }


        if (studentFee.getFeeStructure() != null) {
            builder.feeStructureId(studentFee.getFeeStructure().getId())
                    .feeStructureName(String.valueOf(studentFee.getFeeStructure().getFeeType()));
        }

        return builder.build();
    }

    public StudentFee toEntity(StudentFee existingFee, StudentFeeRequestDTO requestDto) {
        if (requestDto == null || existingFee == null) {
            return null;
        }

        existingFee.setTotalAmount(requestDto.getTotalAmount());
        existingFee.setPaidAmount(requestDto.getPaidAmount());
        existingFee.setRemainingAmount(requestDto.getRemainingAmount());
        existingFee.setDueDate(requestDto.getDueDate());
        existingFee.setStatus(StudentFeeStatus.valueOf(requestDto.getStatus()));
        return existingFee;
    }

    public StudentFee toPartialUpdateEntity(StudentFee existingFee, StudentFeeRequestDTO requestDto) {
        if (requestDto == null || existingFee == null) {
            return null;
        }

        if (requestDto.getTotalAmount() != null) {
            existingFee.setTotalAmount(requestDto.getTotalAmount());
        }
        if (requestDto.getPaidAmount() != null) {
            existingFee.setPaidAmount(requestDto.getPaidAmount());
        }
        if (requestDto.getRemainingAmount() != null) {
            existingFee.setRemainingAmount(requestDto.getRemainingAmount());
        }
        if (requestDto.getDueDate() != null) {
            existingFee.setDueDate(requestDto.getDueDate());
        }
        if (requestDto.getStatus() != null) {
            existingFee.setStatus(StudentFeeStatus.valueOf(requestDto.getStatus()));
        }
        return existingFee;
    }

    public List<StudentFeeResponseDTO> toDTOList(List<StudentFee> studentFees) {
        if(studentFees.isEmpty()) return null;

        return studentFees.stream().map(this::toDTO).toList();
    }
}