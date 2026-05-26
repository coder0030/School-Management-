package com.example.demo.ENTITY;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "exams",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"exam_name","exam_type","academic_year","class_id"}
                )
        }
)
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exam_name", nullable = false)
    private String examName;

    @Column(name = "exam_type", nullable = false)
    private String examType;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(nullable = false)
    private int semester;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_marks")
    private int maxMarks;

    @Column(name = "passing_marks")
    private int passingMarks;

    @Column(name = "obtained_marks")
    private Integer obtainedMarks;  // Use Integer to allow null before grading

    @OneToMany(mappedBy = "exam", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Marks> marksList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    public void addMarks(Marks marks) {
        marksList.add(marks);
        marks.setExam(this);
    }

    public void removeMarks(Marks marks) {
        marksList.remove(marks);
        marks.setExam(null);
    }

    @PrePersist
    protected void create() {
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void updated() {
        this.updatedAt = LocalDate.now();
    }
}