package com.example.demo.ENTITY;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "classes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"class_name","section","room_no"})
        }
)
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "class_name", nullable = false)
    private String className;

    private String section;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "room_no")
    private String roomNo;

    @Column(nullable = false)
    private Integer capacity = 40;

    @OneToMany(mappedBy = "classEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassTeacher> classTeacherList = new ArrayList<>(); // Assuming you have a Teacher entity for class teachers
//
//    @OneToMany(mappedBy = "classEntity", orphanRemoval = true)
//    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassSubject> classSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Exam> examsList = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FeeStructure> FeeStructureList = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> studentList = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timetable> timeTables = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentFee> studentFeesList = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeePayment> feePaymentList = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeacherSubject> teacherSubjectList = new ArrayList<>();

    public void addTeacherSubject(TeacherSubject teacherSubject) {
        teacherSubjectList.add(teacherSubject);
        teacherSubject.setClassEntity(this);
    }

    public void removeTeacherSubject(TeacherSubject teacherSubject) {
        teacherSubjectList.remove(teacherSubject);
        teacherSubject.setClassEntity(null);
    }

    public void addFeePayments(FeePayment feePayment) {
        feePaymentList.add(feePayment);
        feePayment.setClassEntity(this);
    }

    public void removeFeePayments(FeePayment feePayment) {
        feePaymentList.remove(feePayment);
        feePayment.setClassEntity(null);
    }

    public void addStudentFee(StudentFee studentFee) {
        studentFeesList.add(studentFee);
        studentFee.setClassEntity(this);
    }

    public void addTimetables(Timetable timetable) {
        timeTables.add(timetable);
        timetable.setClassEntity(this);
    }

    public void removeTimetables(Timetable timetable) {
        timeTables.remove(timetable);
        timetable.setClassEntity(null);
    }

    public void addExam(Exam exam) {
        examsList.add(exam);
        exam.setClassEntity(this);
    }

    public void removeExam(Exam exam) {
        examsList.remove(exam);
        exam.setClassEntity(null);
    }

    public void addClassStudent(Student student) {
        studentList.add(student);
        student.setClassEntity(this);
    }

    public void removeClassStudent(Student student) {
        studentList.remove(student);
        student.setClassEntity(null);
    }

    public void addFeesStructure(FeeStructure feeStructure) {
        FeeStructureList.add(feeStructure);
        feeStructure.setClassEntity(this);
    }

    public void removeFeesStructure(FeeStructure feeStructure) {
        FeeStructureList.remove(feeStructure);
        feeStructure.setClassEntity(null);
    }

    public void addClassTeacher(ClassTeacher classTeacher) {
        classTeacherList.add(classTeacher);
        classTeacher.setClassEntity(this);
    }

    public void removeClassTeacher(ClassTeacher classTeacher) {
        classTeacherList.remove(classTeacher);
        classTeacher.setClassEntity(null);
    }

    public void addClassSubjects(ClassSubject classSubject) {
        classSubjects.add(classSubject);
        classSubject.setClassEntity(this);
    }

    public void removeClassSubjects(ClassSubject classSubject) {
        classSubjects.remove(classSubject);
        classSubject.setClassEntity(null);
    }

}