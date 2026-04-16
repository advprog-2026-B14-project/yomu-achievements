package id.ac.ui.cs.advprog.yomuachievement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_achievements")
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // user_id tetap String karena ini mereferensikan ID dari Modul Auth
    @Column(name = "user_id", nullable = false)
    private String userId;

    // Relasi Foreign Key langsung ke objek Achievement!
    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "tanggal_didapat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tanggalDidapat = new Date(); // Otomatis terisi waktu saat ini

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "pin_order")
    private Integer pinOrder;

    public UserAchievement() {}
}