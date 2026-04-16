package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomuachievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomuachievement.repository.DailyMissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterDataServiceImpl implements MasterDataService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Override
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public DailyMission createDailyMission(DailyMission dailyMission) {
        return dailyMissionRepository.save(dailyMission);
    }
}