package org.example.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainerId;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType Specialization;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Trainer(TrainingType specialization, User user) {
        Specialization = specialization;
        this.user = user;
    }

}
