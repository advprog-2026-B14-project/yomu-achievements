package id.ac.ui.cs.advprog.yomuachievement.controller;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = "http://localhost:3000")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping
    public List<Achievement> getAllAchievements() {
        return achievementService.findAllAchievements();
    }
}