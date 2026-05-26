package com.example.demo.Repository;

import com.example.demo.ENTITY.FeeStructure;
import com.example.demo.Helper.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    boolean existsByFeeTypeAndClassEntity_Id(FeeType feeType, Long classId);

    boolean existsByClassEntity_IdAndFeeType(Long classId, FeeType feeType);

    boolean existsByFeeTypeAndClassEntity_IdAndIdNot(FeeType feeType, Long classId, Long currentId);
}
