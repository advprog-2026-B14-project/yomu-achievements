package id.ac.ui.cs.advprog.yomuachievement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_gamification_stats")
public class UserGamificationStat {

    // Karena user_id itu unik 1 user = 1 stats, kita jadikan dia Primary Key
    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "level")
    private Integer level = 1;

    public UserGamificationStat() {}
}