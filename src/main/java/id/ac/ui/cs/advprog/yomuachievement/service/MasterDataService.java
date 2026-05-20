package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;

import id.ac.ui.cs.advprog.yomuachievement.dto.AchievementDto;
import id.ac.ui.cs.advprog.yomuachievement.dto.DailyMissionDto;

import java.util.List;
import java.util.UUID;

public interface MasterDataService {
    List<Achievement> getAllAchievements();
    Achievement createAchievement(AchievementDto dto);
    Achievement updateAchievement(UUID id, AchievementDto dto);
    void deleteAchievement(UUID id);

    List<DailyMission> getAllDailyMissions();
    DailyMission createDailyMission(DailyMissionDto dto);
    DailyMission updateDailyMission(UUID id, DailyMissionDto dto);
    void deleteDailyMission(UUID id);
}