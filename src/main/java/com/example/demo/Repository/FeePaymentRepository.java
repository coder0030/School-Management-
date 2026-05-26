package com.example.demo.Repository;

import com.example.demo.ENTITY.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findByStudentFee_Id(Long studentFeeId);

    Optional<FeePayment> findByReceiptNumber(String receiptNumber);

    Optional<FeePayment> findById(Long studentFeeId);

    boolean existsByReceiptNumber(String receiptNumber);
}