package com.example.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassDTO {

    private Long id;

    private String className;

    private String section;

    private String academicYear;

    private String roomNo;

    private Integer capacity;

}
