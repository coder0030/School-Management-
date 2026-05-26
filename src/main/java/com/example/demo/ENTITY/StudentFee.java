package com.example.demo.ENTITY;

import com.example.demo.Helper.StudentFeeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_fee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private ClassEntity classEntity;

    @ManyToOne
    private FeeStructure feeStructure;

    private Double totalAmount;

    private Double paidAmount;

    private Double remainingAmount;

    private LocalDate dueDate;

    private StudentFeeStatus status;

    @OneToMany(mappedBy = "studentFee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeePayment> feePaymentList = new ArrayList<>();

    public void addFeePayments(FeePayment feePayment) {
        feePaymentList.add(feePayment);
        feePayment.setStudentFee(this);
    }

    public void removeFeePayments(FeePayment feePayment) {
        feePaymentList.remove(feePayment);
        feePayment.setStudentFee(null);
    }

}