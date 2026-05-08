package id.ac.ui.cs.advprog.yomuachievement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PinAchievementRequest {
    private String userId;
    private UUID achievementId;
    private Integer pinOrder;
}
