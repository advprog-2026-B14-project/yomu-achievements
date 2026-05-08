package id.ac.ui.cs.advprog.yomuachievement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String userId;
    private Integer level;
    private Integer totalPoints;
    private List<PinnedAchievementDto> pinnedAchievements;
}
