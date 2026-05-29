package com.example.demo.ENTITY;

import com.example.demo.Helper.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "class_teacher")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "assignment_date")
    private LocalDate assignmentDate;

    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    private Role roleType;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "academic_year")
    private String academicYear;

    public void addRoles(Role role) {
        if(!teacher.getUser().getRoles().contains(role)) {
            teacher.getUser().getRoles().add(role);
        }
    }

    public void removeRoles(Role role) {
        if(teacher.getUser().getRoles().contains(role)) {
            teacher.getUser().getRoles().remove(role);
        }
    }

    @PrePersist
    protected void create() {
        assignmentDate = LocalDate.now();
    }
}