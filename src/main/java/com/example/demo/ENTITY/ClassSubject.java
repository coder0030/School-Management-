package com.example.demo.ENTITY;

import com.fasterxml.jackson.annotation.JacksonInject;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Missing fields for schedule
// You have scheduleDay and scheduleTime in uniqueConstraint but not in entity!
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"class_id","subject_id","teacher_id"}
                )
        }
)
public class ClassSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private LocalDateTime updatedAt;
    private LocalDateTime assignedAt;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    public Subject getSubject() {
        return subject;
    }

    @PrePersist
    protected void create() {
        assignedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void update() {
        updatedAt = LocalDateTime.now();
    }
}