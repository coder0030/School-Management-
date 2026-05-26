package com.example.demo.Mapper;

import com.example.demo.DTO.FeeStructureDTO;
import com.example.demo.ENTITY.FeeStructure;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.RequestDTO.FeeStructureRequestDTO;
import org.springframework.stereotype.Component;

import javax.swing.plaf.PanelUI;
import java.util.List;

@Component

public class FeesStructureMapper {
    public FeeStructureDTO toDTO(FeeStructure feeStructure) {
        if(feeStructure == null) return null;

        return FeeStructureDTO.builder()
                .id(feeStructure.getId())
                .feeType(feeStructure.getFeeType())
                .amount(feeStructure.getAmount())
                .frequency(feeStructure.getFrequency())
                .dueDate(feeStructure.getDueDate())
                .classId(feeStructure.getClassEntity().getId())
                .build();
    }

    public FeeStructure toUpdate(FeeStructureRequestDTO requestDTO, FeeStructure feeStructure) {
        if(requestDTO.getFeeType() != null && requestDTO.getAmount() != null &&
                requestDTO.getFrequency() != null && requestDTO.getDueDate() != null) {

            feeStructure.setFeeType(requestDTO.getFeeType());
            feeStructure.setAmount(requestDTO.getAmount());
            feeStructure.setFrequency(requestDTO.getFrequency());
            feeStructure.setDueDate(requestDTO.getDueDate());
        } else {
            throw new IncompleteDataException("Data is incompleted, Request Body is not filled.");
        }

        return feeStructure;
    }

    public FeeStructure toPartialUpdate(FeeStructureRequestDTO requestDTO, FeeStructure feeStructure) {
        if(requestDTO == null) return null;

        if(requestDTO.getFeeType() != null) {
            feeStructure.setFeeType(requestDTO.getFeeType());
        }

        if(requestDTO.getAmount() != null) {
            feeStructure.setAmount(requestDTO.getAmount());

        }

        if(requestDTO.getFrequency() != null) {
            feeStructure.setFrequency(requestDTO.getFrequency());

        }

        if(requestDTO.getDueDate() != null) {
            feeStructure.setDueDate(requestDTO.getDueDate());

        }
        return feeStructure;
    }

    public List<FeeStructureDTO> toDTOList(List<FeeStructure> feeStructureList) {
        if(feeStructureList.isEmpty()) return null;

        return feeStructureList.stream().map(this::toDTO).toList();
    }
}
