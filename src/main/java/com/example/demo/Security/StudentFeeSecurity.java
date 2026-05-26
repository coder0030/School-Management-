package com.example.demo.Security;

import com.example.demo.ENTITY.StudentFee;
import com.example.demo.ENTITY.User;
import com.example.demo.Repository.StudentFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("studentFeeSecurity")
@RequiredArgsConstructor
public class StudentFeeSecurity {

    private final StudentFeeRepository studentFeeRepository;

    public boolean isOwner(Long studentFeeId) {

        User user = SecurityUtil.getCurrentUserDetails();

        StudentFee fee = studentFeeRepository.findById(studentFeeId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));

        return fee.getStudent().getId().equals(user.getId());
    }
}