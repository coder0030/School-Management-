package com.example.demo.ENTITY;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "Timetable",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"class_id","day_of_week","period_number"})
        }
)
public class Timetable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "period_number")
    private int periodNumber;

    private LocalTime startTime;
    private LocalTime endTime;

    public void addAttendances(Attendance attendance) {
        attendances.add(attendance);
        attendance.setTimetable(this);
    }

    public void removeAttendances(Attendance attendance) {
        attendances.remove(attendance);
        attendance.setTimetable(null);
    }
}