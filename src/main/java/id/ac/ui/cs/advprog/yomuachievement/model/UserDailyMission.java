package id.ac.ui.cs.advprog.yomuachievement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_daily_missions")
public class UserDailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    // Relasi Foreign Key langsung ke objek DailyMission
    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private DailyMission mission;

    @Column(name = "progress")
    private Integer progress = 0;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "tanggal_selesai")
    private LocalDateTime tanggalSelesai;

    public UserDailyMission() {}
}