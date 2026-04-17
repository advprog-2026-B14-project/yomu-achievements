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

    @Override
    public List<Achievement> findAllAchievements() {
        return achievementRepository.findAll();
    }

    // --- LOGIKA UTAMA MILESTONE 50% ---

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
            
            // Cek Auto-Unlock: Jika progres >= target di tabel master
            if (userRecord.getCurrentProgress() >= master.getMilestoneTarget()) { 
                userRecord.setIsUnlocked(true);
                userRecord.setTanggalDidapat(LocalDateTime.now());
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
            
            // Cek Auto-Unlock: Jika progres >= target
            if (userRecord.getProgress() >= master.getMilestoneTarget()) { 
                userRecord.setIsCompleted(true);
                userRecord.setTanggalSelesai(LocalDateTime.now());
            }
            userDailyMissionRepository.save(userRecord);
        }
    }
}