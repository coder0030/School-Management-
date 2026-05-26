package com.example.demo.ENTITY;

import com.example.demo.Helper.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Admin extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String department;

    private Boolean isActive = true;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

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
    protected void create() {
        user.getRoles().add(Role.ROLE_ADMIN);
    }
}