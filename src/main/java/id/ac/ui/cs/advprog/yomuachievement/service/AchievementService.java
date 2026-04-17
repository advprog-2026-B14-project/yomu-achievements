package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import java.util.List;
import java.util.UUID;

public interface AchievementService {
    List<Achievement> findAllAchievements();
    void updateAchievementProgress(String userId, UUID achievementId);
    void updateMissionProgress(String userId, UUID missionId);
}