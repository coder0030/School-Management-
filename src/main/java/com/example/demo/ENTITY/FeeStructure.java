package com.example.demo.ENTITY;
import com.example.demo.Helper.FeeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "FeeStructure",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"feeType","class_id"})
        }
)
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_type")
    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

}