package com.example.demo.Service;



import com.example.demo.DTO.StudentFeeResponseDTO;
import com.example.demo.RequestDTO.StudentFeeRequestDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface StudentFeeService {
    StudentFeeResponseDTO createStudentFee(StudentFeeRequestDTO requestDto);
    StudentFeeResponseDTO getStudentFeeById(Long id);
    List<StudentFeeResponseDTO> getAllStudentFees();
    StudentFeeResponseDTO updateStudentFee(Long id, StudentFeeRequestDTO requestDto);
    List<StudentFeeResponseDTO> getFeesByStudentId(Long studentId);
    List<StudentFeeResponseDTO> getFeesByClassId(Long classId);
    List<StudentFeeResponseDTO> getFeeByStudentClassAndRollNo(Long classId, int rollNo);
    List<StudentFeeResponseDTO> getFeeByStudentMobile(String mobileNo, @Valid StudentFeeRequestDTO requestDTO);
}