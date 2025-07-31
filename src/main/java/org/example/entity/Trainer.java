package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
@Table(name = "trainer") // Explicit table name (if your DB uses lowercase or snake_case)
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainerid") // Match DB column name exactly
    private Long trainerId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "trainingtypeid", nullable = false) // Match DB column name
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userid", nullable = false, unique = true) // Match DB column name
    private User user;

    public Trainer(TrainingType specialization, User user) {
        this.specialization = specialization;
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
