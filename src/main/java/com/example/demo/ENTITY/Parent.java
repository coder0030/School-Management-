package com.example.demo.ENTITY;

import com.example.demo.Helper.Role;
import com.nimbusds.openid.connect.sdk.claims.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "parents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Parent extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 50)
    private String occupation;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Student> studentList = new ArrayList<>();

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

    public void addStudents(Student student) {
        studentList.add(student);
        student.setParent(this);
    }

    public void removeStudents(Student student) {
        studentList.remove(student);
        student.setParent(null);
    }

    @PrePersist
    protected void update() {
        user.getRoles().add(Role.ROLE_PARENT);
        ;
    }
}