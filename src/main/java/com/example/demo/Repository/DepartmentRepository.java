package com.example.demo.Repository;

import com.example.demo.DTO.DepartmentDTO;
import com.example.demo.ENTITY.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
