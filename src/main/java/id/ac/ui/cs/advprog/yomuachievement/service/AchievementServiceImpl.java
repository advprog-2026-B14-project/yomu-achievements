package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.dto.PinnedAchievementDto;
import id.ac.ui.cs.advprog.yomuachievement.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomuachievement.model.*;
import id.ac.ui.cs.advprog.yomuachievement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
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

    @Override
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

        Optional<UserAchievement> existingPinned = userAchievementRepository
            .findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(userId).stream()
            .filter(ua -> ua.getPinOrder().equals(pinOrder))
            .findFirst();

        if (existingPinned.isPresent()) {
            UserAchievement oldPinned = existingPinned.get();
            oldPinned.setIsPinned(false);
            oldPinned.setPinOrder(null);
            userAchievementRepository.save(oldPinned);
        }

        userRecord.setIsPinned(true);
        userRecord.setPinOrder(pinOrder);
        userAchievementRepository.save(userRecord);
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        UserGamificationStat stat = userGamificationStatRepository.findById(userId)
                .orElseGet(() -> {
                    UserGamificationStat newStat = new UserGamificationStat();
                    newStat.setUserId(userId);
                    newStat.setTotalPoints(0);
                    newStat.setLevel(1);
                    return newStat;
                });

        List<UserAchievement> pinned = userAchievementRepository.findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(userId);

        List<PinnedAchievementDto> pinnedDtos = pinned.stream()
                .map(ua -> PinnedAchievementDto.builder()
                        .id(ua.getAchievement().getId())
                        .nama(ua.getAchievement().getNama())
                        .deskripsi(ua.getAchievement().getDeskripsi())
                        .milestoneTarget(ua.getAchievement().getMilestoneTarget())
                        .poinReward(ua.getAchievement().getPoinReward())
                        .pinOrder(ua.getPinOrder())
                        .build())
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .userId(userId)
                .level(stat.getLevel())
                .totalPoints(stat.getTotalPoints())
                .pinnedAchievements(pinnedDtos)
                .build();
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetAllDailyMissions() {
        userDailyMissionRepository.deleteAll();
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