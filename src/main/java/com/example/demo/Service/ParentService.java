package com.example.demo.Service;


import com.example.demo.DTO.ParentResponseDTO;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.RequestDTO.ParentRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ParentService {

    ParentResponseDTO createParent(ParentRequestDTO requestDTO);

    ParentResponseDTO getParentById(Long id);

    ParentResponseDTO getParentByCode(String parentCode);

    ParentResponseDTO getParentByEmail(String email);

    Page<ParentResponseDTO> getAllParents(int pageNo, int pageSize);

    Page<ParentResponseDTO> getParentsWithMultipleChildren(int pageNo, int pageSize);

    ParentResponseDTO updateParent(Long id, ParentRequestDTO requestDTO);

    ParentResponseDTO partialUpdateParent(Long id, ParentRequestDTO requestDTO);

        void deleteParent(Long id);

    ParentResponseDTO addStudentToParent(Long parentId, Long studentId);

    ParentResponseDTO removeStudentFromParent(Long parentId, Long studentId);

    List<StudentDTO> getAllChildrenOfParent(Long parentId);
}