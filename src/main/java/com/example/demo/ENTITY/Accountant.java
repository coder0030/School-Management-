package com.example.demo.ENTITY;

import com.example.demo.Helper.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Accountant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Accountant extends Person{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isActive = false;
    private LocalDateTime assignedDate;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
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
        this.assignedDate = LocalDateTime.now();
        isActive = true;
    }
}
