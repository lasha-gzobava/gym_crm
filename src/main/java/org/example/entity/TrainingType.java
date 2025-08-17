package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "training_type")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainingtypeid")
    private long trainingTypeId;

    @Column(name = "trainingtypename", nullable = false, unique = true)
    private String trainingTypeName;

    public TrainingType(long l, String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
