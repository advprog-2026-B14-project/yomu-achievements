package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;

public interface MasterDataService {
    Achievement createAchievement(Achievement achievement);
    DailyMission createDailyMission(DailyMission dailyMission);
}