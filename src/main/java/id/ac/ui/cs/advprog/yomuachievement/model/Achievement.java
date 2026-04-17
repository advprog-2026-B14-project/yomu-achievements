package id.ac.ui.cs.advprog.yomuachievement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter 
@Setter
@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String nama;

    @Column(name = "description", columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "milestone_target", nullable=false)
    private Integer milestoneTarget = 0; // Default 0

    @Column(name = "poin_reward")
    private Integer poinReward = 0; // Default 0

    @Column(name = "milestone_type", nullable = false)
    private String milestoneType;

    @Column(name = "badge_url", columnDefinition = "TEXT")
    private String badgeUrl;

    public Achievement() {}
}