package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name = "trainee") // Ensures matching DB table name
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "traineeid") // Match DB naming convention
    private Long traineeId;

    @Column(name = "dateofbirth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userid", nullable = false, unique = true) // Match DB
    private User user;

    @ManyToMany
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "traineeid"),
            inverseJoinColumns = @JoinColumn(name = "trainerid")
    )
    private List<Trainer> trainers = new ArrayList<>();

    public Trainee(LocalDate dateOfBirth, String address, User user) {
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.user = user;
    }
}
