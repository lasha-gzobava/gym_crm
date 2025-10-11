package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@NoArgsConstructor
@Data
@Entity
@Table(name = "training")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainingid")
    private Long trainingId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "traineeid", nullable = false)
    private Trainee trainee;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "trainerid", nullable = false)
    private Trainer trainer;

    @Column(name = "trainingname", nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "trainingtypeid")
    private TrainingType trainingType;

    @Column(name = "trainingdate", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "trainingduration", nullable = false)
    private Long trainingDuration;

    public Training(
            Trainee trainee,
            Trainer trainer,
            String trainingName,
            TrainingType trainingType,
            LocalDate trainingDate,
            Long trainingDuration
    ) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
