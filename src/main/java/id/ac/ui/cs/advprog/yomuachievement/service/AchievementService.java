package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import java.util.List;

public interface AchievementService {
    List<Achievement> findAllAchievements();
}