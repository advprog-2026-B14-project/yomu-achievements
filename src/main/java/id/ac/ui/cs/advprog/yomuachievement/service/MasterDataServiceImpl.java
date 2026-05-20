package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.dto.AchievementDto;
import id.ac.ui.cs.advprog.yomuachievement.dto.DailyMissionDto;
import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomuachievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomuachievement.repository.DailyMissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MasterDataServiceImpl implements MasterDataService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @Override
    public Achievement createAchievement(AchievementDto dto) {
        Achievement achievement = new Achievement();
        achievement.setNama(dto.getNama());
        achievement.setDeskripsi(dto.getDeskripsi());
        achievement.setMilestoneTarget(dto.getMilestoneTarget());
        achievement.setPoinReward(dto.getPoinReward());
        return achievementRepository.save(achievement);
    }

    @Override
    public Achievement updateAchievement(UUID id, AchievementDto dto) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement tidak ditemukan"));
        achievement.setNama(dto.getNama());
        achievement.setDeskripsi(dto.getDeskripsi());
        achievement.setMilestoneTarget(dto.getMilestoneTarget());
        achievement.setPoinReward(dto.getPoinReward());
        return achievementRepository.save(achievement);
    }

    @Override
    public void deleteAchievement(UUID id) {
        achievementRepository.deleteById(id);
    }

    @Override
    public List<DailyMission> getAllDailyMissions() {
        return dailyMissionRepository.findAll();
    }

    @Override
    public DailyMission createDailyMission(DailyMissionDto dto) {
        DailyMission mission = new DailyMission();
        mission.setNamaMisi(dto.getNama());
        mission.setMilestoneTarget(dto.getMilestoneTarget());
        mission.setPoinReward(dto.getPoinReward());
        return dailyMissionRepository.save(mission);
    }

    @Override
    public DailyMission updateDailyMission(UUID id, DailyMissionDto dto) {
        DailyMission mission = dailyMissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Daily Mission tidak ditemukan"));
        mission.setNamaMisi(dto.getNama());
        mission.setMilestoneTarget(dto.getMilestoneTarget());
        mission.setPoinReward(dto.getPoinReward());
        return dailyMissionRepository.save(mission);
    }

    @Override
    public void deleteDailyMission(UUID id) {
        dailyMissionRepository.deleteById(id);
    }
}