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
    @Column(name = "trainingtypeid") // maps to your DB column exactly
    private long trainingTypeId;

    @Column(name = "trainingtypename", nullable = false, unique = true) // maps to your DB column exactly
    private String trainingTypeName;

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
