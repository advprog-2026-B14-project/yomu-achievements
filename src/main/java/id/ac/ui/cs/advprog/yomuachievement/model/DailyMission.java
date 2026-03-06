package id.ac.ui.cs.advprog.yomuachievement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "daily_missions")
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nama_misi", nullable = false, unique = true)
    private String namaMisi;

    @Column(name = "target", nullable = false)
    private Integer target;

    @Column(name = "poin_reward", nullable = false)
    private Integer poinReward;

    public DailyMission() {}
}