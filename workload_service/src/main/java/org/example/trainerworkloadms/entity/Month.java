package org.example.trainerworkloadms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "months")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Month {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "month_id")
    private Long monthId;
    @Column(name = "month_number")
    private int month;
    private int duration;

    @ManyToOne
    @JoinColumn(name = "year_id")
    private Year year;
}
