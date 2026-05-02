package id.ac.ui.cs.advprog.yomuachievement.controller;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomuachievement.service.MasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/master")
public class AdminMasterDataController {

    @Autowired
    private MasterDataService masterDataService;

    // Endpoint untuk inisialisasi Achievement
    @PostMapping("/achievements")
    public ResponseEntity<Achievement> addAchievement(@RequestBody Achievement achievement) {
        Achievement savedAchievement = masterDataService.createAchievement(achievement);
        return ResponseEntity.ok(savedAchievement);
    }

    // Endpoint untuk inisialisasi Daily Mission
    @PostMapping("/missions")
    public ResponseEntity<DailyMission> addDailyMission(@RequestBody DailyMission dailyMission) {
        DailyMission savedMission = masterDataService.createDailyMission(dailyMission);
        return ResponseEntity.ok(savedMission);
    }
}