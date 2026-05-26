package com.example.demo.Service;

import com.example.demo.DTO.FeeStructureDTO;
import com.example.demo.RequestDTO.FeeStructureRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FeeStructureService {
    FeeStructureDTO create(@Valid FeeStructureRequestDTO dto);

    FeeStructureDTO getById(Long id);

    FeeStructureDTO updateById(Long id, @Valid FeeStructureRequestDTO dto);

    FeeStructureDTO partialUpdateById(Long id, FeeStructureRequestDTO dto);

    void deleteById(Long id);

    void deleteAll();

    Page<FeeStructureDTO> getAll(int pageNo, int pageSize);
}
