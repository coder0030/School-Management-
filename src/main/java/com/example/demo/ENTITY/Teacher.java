package com.example.demo.ENTITY;

import com.example.demo.Helper.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@SuperBuilder
@Entity
@Table(
        name = "Teachers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email", "phone"})
        }
)
public class Teacher extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private LocalDateTime localDateTime;

    private String qualification;

    private String specialization;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassTeacher> classTeacherList = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timetable> timeTables = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherSubject> teacherSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassSubject> classSubjects = new ArrayList<>();

    public void addTeacherSubject(TeacherSubject ts) {
        teacherSubjects.add(ts);
        ts.setTeacher(this);
    }

    public void removeTeacherSubject(TeacherSubject ts){
        teacherSubjects.remove(ts);
        ts.setTeacher(null);
    }

    public void addTimetables(Timetable timetable) {
        timeTables.add(timetable);
        timetable.setTeacher(this);
    }

    public void removeTimetables(Timetable timetable) {
        timeTables.remove(timetable);
        timetable.setTeacher(null);
    }

    public void addClassTeacher(ClassTeacher classTeacher) {
        classTeacherList.add(classTeacher);
        classTeacher.setTeacher(this);
    }

    public void removeClassTeacher(ClassTeacher classTeacher) {
        classTeacherList.remove(classTeacher);
        classTeacher.setTeacher(null);
    }

    public String getTeacherName() {
        if(getFirstName() != null) {
            return getFirstName() + " " + getLastName();
        }
        return null;
    }

    public void addRoles(Role role) {
        if(!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }
    }

    @PrePersist
    protected void onCreate() {
        user.getRoles().add(Role.ROLE_TEACHER);
        localDateTime = LocalDateTime.now();
    }

    public void removeRoles(Role role) {
        if(user.getRoles().contains(role)) {
            user.getRoles().remove(role);
        }
    }

    public void addClassSubjects(ClassSubject classSubject) {
        classSubjects.add(classSubject);
        classSubject.setTeacher(this);
    }

    public void removeClassSubjects(ClassSubject classSubject) {
        classSubjects.remove(classSubject);
        classSubject.setTeacher(null);
    }
}