package org.example.entity;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trainee {
    private Long id;
    private String dateOfBirth;
    private String address;
    private Long userId;
}
