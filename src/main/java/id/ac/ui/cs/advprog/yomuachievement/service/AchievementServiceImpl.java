package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.*;
import id.ac.ui.cs.advprog.yomuachievement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AchievementServiceImpl implements AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Autowired
    private UserDailyMissionRepository userDailyMissionRepository;

    @Autowired
    private UserGamificationStatRepository userGamificationStatRepository;

    @Override
    public List<Achievement> findAllAchievements() {
        return achievementRepository.findAll();
    }

    public void updateAchievementProgress(String userId, UUID achievementId) {
        Achievement master = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement tidak ditemukan"));

        UserAchievement userRecord = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)
                .orElseGet(() -> {
                    UserAchievement newRecord = new UserAchievement();
                    newRecord.setUserId(userId);
                    newRecord.setAchievement(master);
                    newRecord.setCurrentProgress(0);
                    newRecord.setIsUnlocked(false);
                    return newRecord;
                });

        if (!userRecord.getIsUnlocked()) {
            userRecord.setCurrentProgress(userRecord.getCurrentProgress() + 1);
            
            if (userRecord.getCurrentProgress() >= master.getMilestoneTarget()) { 
                userRecord.setIsUnlocked(true);
                userRecord.setTanggalDidapat(LocalDateTime.now());
                
                // Distribusi Poin & Leveling
                distributePointsAndLevelUp(userId, master.getPoinReward());
            }
            userAchievementRepository.save(userRecord);
        }
    }

    public void updateMissionProgress(String userId, UUID missionId) {
        DailyMission master = dailyMissionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Misi tidak ditemukan"));

        UserDailyMission userRecord = userDailyMissionRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseGet(() -> {
                    UserDailyMission newRecord = new UserDailyMission();
                    newRecord.setUserId(userId);
                    newRecord.setMission(master);
                    newRecord.setProgress(0);
                    newRecord.setIsCompleted(false);
                    return newRecord;
                });

        if (!userRecord.getIsCompleted()) {
            userRecord.setProgress(userRecord.getProgress() + 1);
            
            if (userRecord.getProgress() >= master.getMilestoneTarget()) { 
                userRecord.setIsCompleted(true);
                userRecord.setTanggalSelesai(LocalDateTime.now());

                // Distribusi Poin & Leveling
                distributePointsAndLevelUp(userId, master.getPoinReward());
            }
            userDailyMissionRepository.save(userRecord);
        }
    }

    @Override
    public void pinAchievement(String userId, UUID achievementId, Integer pinOrder) {
        if (pinOrder == null || pinOrder < 1 || pinOrder > 3) {
            throw new IllegalArgumentException("Pin order harus antara 1 dan 3");
        }

        UserAchievement userRecord = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow(() -> new RuntimeException("Record UserAchievement tidak ditemukan"));

        if (!userRecord.getIsUnlocked()) {
            throw new RuntimeException("Hanya achievement yang sudah terbuka yang bisa di-pin");
        }

        userRecord.setIsPinned(true);
        userRecord.setPinOrder(pinOrder);
        userAchievementRepository.save(userRecord);
    }

    /**
     * Helper method to handle Point Distribution and Leveling logic.
     */
    private void distributePointsAndLevelUp(String userId, Integer pointReward) {
        if (pointReward == null) pointReward = 0;

        UserGamificationStat stat = userGamificationStatRepository.findById(userId)
                .orElseGet(() -> {
                    UserGamificationStat newStat = new UserGamificationStat();
                    newStat.setUserId(userId);
                    return newStat;
                });

        stat.addPoints(pointReward);
        userGamificationStatRepository.save(stat);
    }
}