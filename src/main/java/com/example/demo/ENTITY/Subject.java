package com.example.demo.ENTITY;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", nullable = false, unique = true)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "theory_marks")
    private Integer theoryMarks;

    @Column(name = "practical_marks")
    private Integer practicalMarks;

    @OneToMany(mappedBy = "subject", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Attendance> attendanceList = new ArrayList<>();

    @OneToMany(mappedBy = "subject", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Marks> marksList = new ArrayList<>();

    @OneToMany(mappedBy = "subject", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassSubject> classSubjectList = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timetable> timeTables = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherSubject> teacherSubjects = new ArrayList<>();

    public void addTeacherSubject(TeacherSubject ts){
        teacherSubjects.add(ts);
        ts.setSubject(this);
    }

    public void removeTeacherSubject(TeacherSubject ts){
        teacherSubjects.add(ts);
        ts.setSubject(null);
    }

    public void addTimetables(Timetable timetable) {
        timeTables.add(timetable);
        timetable.setSubject(this);
    }

    public void removeTimetables(Timetable timetable) {
        timeTables.remove(timetable);
        timetable.setSubject(null);
    }

    public void addClassSubject(ClassSubject classSubject) {
        classSubjectList.add(classSubject);
        classSubject.setSubject(this);
    }

    public void removeClassSubject(ClassSubject classSubject) {
        classSubjectList.remove(classSubject);
        classSubject.setSubject(null);
    }

    public void addMarks(Marks marks) {
        marksList.add(marks);
        marks.setSubject(this);
    }

    public void removeMarks(Marks marks) {
        marksList.remove(marks);
        marks.setSubject(null);
    }
}