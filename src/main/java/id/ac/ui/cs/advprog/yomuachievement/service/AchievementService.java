package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.dto.PinnedAchievementDto;
import id.ac.ui.cs.advprog.yomuachievement.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import java.util.List;
import java.util.UUID;

public interface AchievementService {
    List<Achievement> findAllAchievements();
    void updateAchievementProgress(String userId, UUID achievementId);
    void updateMissionProgress(String userId, UUID missionId);
    void pinAchievement(String userId, UUID achievementId, Integer pinOrder);
    List<PinnedAchievementDto> getUnlockedAchievements(String userId);
    UserProfileResponse getUserProfile(String userId);
    void resetAllDailyMissions();

    /**
     * PERFORMANCE V1 (Slow): Demonstrates the N+1 query anti-pattern.
     * Calculates total combined points for a group of users (a "clan"),
     * but fetches each user's stats in a separate DB round-trip.
     */
    int calculateTotalClanPointsSlow(List<String> clanUserIds);

    /**
     * PERFORMANCE V2 (Optimized): Solves the N+1 problem.
     * Calculates total combined points for a "clan" using a single
     * SQL aggregate query — 1 DB call regardless of clan size.
     */
    int calculateTotalClanPointsOptimized(List<String> clanUserIds);
}