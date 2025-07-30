package org.example.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer)) return false;
        Trainer that = (Trainer) o;
        return getTrainerId() != null && getTrainerId().equals(that.getTrainerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTrainerId());
    }


}
