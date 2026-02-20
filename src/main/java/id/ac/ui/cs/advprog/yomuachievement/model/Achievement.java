package id.ac.ui.cs.advprog.yomuachievement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "achievements_list")
public class Achievement {

    // Getter dan Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama", nullable = false)
    private String nama;

    // Default constructor
    public Achievement() {}

}