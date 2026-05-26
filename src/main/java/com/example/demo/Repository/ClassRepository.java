package com.example.demo.Repository;

import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.Helper.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {


    static int countByRollNo() {
        return 0;
    }

    boolean existsByRoomNo(String roomNo);
    
    boolean existsByFeeStructureList_Id(Long id);
    
    boolean existsByClassNameAndSectionAndIdNot(String className, String section, Long id);

    boolean existsByRoomNoAndIdNot(String roomNo, Long id);
}
