package com.example.demo.ENTITY;

import com.example.demo.Helper.FeeType;
import com.example.demo.Helper.Role;
import com.example.demo.Helper.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "Students",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email", "phone", "rollNo"})
        }
)
public class Student extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private int rollNo;

    private LocalDate admissionDate;

    private LocalDate updatedAt;
    @Enumerated(EnumType.STRING)

    private Status status;

    private String previous_school;

    private Set<Long> feeTypesId = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Attendance> attendanceList = new ArrayList<>();

    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Marks> marksList = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookIssue> bookIssueList = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentFee> studentFeesList = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeePayment> feePaymentList = new ArrayList<>();

    public void addFeePayments(FeePayment feePayment) {
        feePaymentList.add(feePayment);
        feePayment.setStudent(this);
    }

    public void removeFeePayments(FeePayment feePayment) {
        feePaymentList.remove(feePayment);
        feePayment.setStudent(null);
    }

    public void addStudentFee(StudentFee studentFee) {
        studentFeesList.add(studentFee);
        studentFee.setStudent(this);
    }

    public void addBookIssue(BookIssue bookIssue) {
        bookIssueList.add(bookIssue);
        bookIssue.setStudent(this);
    }

    public void addMarks(Marks marks) {
        marksList.add(marks);
        marks.setStudent(this);
    }

    public void removeMarks(Marks marks) {
        marksList.remove(marks);
        marks.setStudent(null);
    }

    public void addAttendance(Attendance attendance) {
        attendanceList.add(attendance);
        attendance.setStudent(this);
    }

    public void removeAttendance(Attendance attendance) {
        attendanceList.remove(attendance);
        attendance.setStudent(null);
    }

    public void removeBookIssue(BookIssue bookIssue) {
        bookIssueList.remove(bookIssue);
        bookIssue.setStudent(null);
    }

    public String getStudentName() {
        if(getFirstName() != null  && getLastName() != null) {
            return getFirstName() + " " + getLastName();
        }
        return null;
    }

    public void addRoles(Role role) {
        if(!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }
    }

    public void removeRoles(Role role) {
        if(user.getRoles().contains(role)) {
            user.getRoles().remove(role);
        }
    }

    @PrePersist
    protected void Create() {
        status = Status.ACTIVE;
        admissionDate = LocalDate.now();
//        user.getRoles().add(Role.ROLE_STUDENT);
    }

    @PreUpdate
    protected void update() {
        updatedAt = LocalDate.now();
    }
}
