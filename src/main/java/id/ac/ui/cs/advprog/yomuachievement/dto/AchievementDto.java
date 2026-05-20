package id.ac.ui.cs.advprog.yomuachievement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AchievementDto {
    private UUID id;
    private String nama;
    private String deskripsi;
    private Integer milestoneTarget;
    private Integer poinReward;
}
