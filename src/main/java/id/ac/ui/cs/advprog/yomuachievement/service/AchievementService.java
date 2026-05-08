package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import java.util.List;
import java.util.UUID;

public interface AchievementService {
    List<Achievement> findAllAchievements();
    void updateAchievementProgress(String userId, UUID achievementId);
    void updateMissionProgress(String userId, UUID missionId);
    void pinAchievement(String userId, UUID achievementId, Integer pinOrder);
    UserProfileResponse getUserProfile(String userId);
}