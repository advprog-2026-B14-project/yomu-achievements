package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementServiceImpl implements AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Override
    public List<Achievement> findAllAchievements() {
        return achievementRepository.findAll();
    }
}