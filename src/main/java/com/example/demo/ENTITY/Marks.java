package com.example.demo.ENTITY;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "marks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id","subject_id","exam_id"})
        }
)
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "marks_obtained")
    private Double marksObtained;

    @Column(name = "grade")
    private String grade;

    @PrePersist
    protected void create() {
        if(marksObtained >= 90) grade = "A+";
        else if(marksObtained >= 80) grade = "A";
        else if(marksObtained >= 70) grade = "B+";
        else if(marksObtained >= 60) grade = "B";
        else grade = "C";
    }
}