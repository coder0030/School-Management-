package com.example.demo.ENTITY;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "LibraryBook",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"title","author"})
        }
)
public class LibraryBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String publisher;

    @Column(name = "total_copies")
    private Integer totalCopies;

    @Column(name = "available_copies")
    private Integer availableCopies;

    @OneToMany(mappedBy = "libraryBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookIssue> bookIssueList = new ArrayList<>();

    public void addBookIssue(BookIssue bookIssue) {
        bookIssueList.add(bookIssue);
        bookIssue.setLibraryBook(this);
    }

    public void removeBookIssue(BookIssue bookIssue) {
        bookIssueList.remove(bookIssue);
        bookIssue.setLibraryBook(null);
    }
}
