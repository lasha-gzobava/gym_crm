package org.example.trainerworkloadms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "trainers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Long trainerId;
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    private List<Year> years;
}
