package org.example.entity;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {
    private Long id;
    private String specialization;
    private Long userId;
}
