package id.ac.ui.cs.advprog.yomuachievement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_achievements")
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "tanggal_didapat")
    private LocalDateTime tanggalDidapat;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "pin_order")
    private Integer pinOrder;

    @Column(name = "progress")
    private Integer currentProgress = 0;

    @Column(name = "is_unlocked")
    private Boolean isUnlocked = false;

    public UserAchievement() {}
}