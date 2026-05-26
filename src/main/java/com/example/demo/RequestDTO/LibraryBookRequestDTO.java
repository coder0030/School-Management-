package com.example.demo.RequestDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryBookRequestDTO {
    private String title;
    private String author;
    private String publisher;
    private Integer totalCopies;
    private Integer availableCopies;
}
