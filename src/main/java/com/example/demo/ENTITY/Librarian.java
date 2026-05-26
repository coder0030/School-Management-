package com.example.demo.ENTITY;

import com.example.demo.Helper.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Entity
@Table(name = "Librarian")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Librarian extends Person {
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
