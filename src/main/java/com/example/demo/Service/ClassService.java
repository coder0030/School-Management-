package com.example.demo.Service;

import com.example.demo.DTO.ClassDTO;
import com.example.demo.RequestDTO.ClassRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassService {
    ClassDTO createClass(@Valid ClassRequestDTO classRequestDTO);

    Page<ClassDTO> getAllClasses(int pageNo, int pageSize);

    ClassDTO getClassById(Long id);

    void deleteClassById(Long id);

    ClassDTO updateClassById(Long id, ClassRequestDTO classRequestDTO);

    ClassDTO partialUpdateClassById(Long id, ClassRequestDTO classRequestDTO);
}
