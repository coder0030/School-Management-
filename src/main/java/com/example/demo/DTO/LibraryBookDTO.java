package com.example.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryBookDTO {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private Integer totalCopies;
    private Integer availableCopies;
}
