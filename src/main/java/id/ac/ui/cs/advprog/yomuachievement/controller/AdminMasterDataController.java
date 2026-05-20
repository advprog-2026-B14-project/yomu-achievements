package id.ac.ui.cs.advprog.yomuachievement.controller;

import id.ac.ui.cs.advprog.yomuachievement.dto.AchievementDto;
import id.ac.ui.cs.advprog.yomuachievement.dto.DailyMissionDto;
import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomuachievement.service.MasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/master")
@CrossOrigin(origins = "*")
public class AdminMasterDataController {

    @Autowired
    private MasterDataService masterDataService;

    // --- ACHIEVEMENTS ---

    @GetMapping("/achievements")
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(masterDataService.getAllAchievements());
    }

    @PostMapping("/achievements")
    public ResponseEntity<Achievement> addAchievement(@RequestBody AchievementDto dto) {
        Achievement savedAchievement = masterDataService.createAchievement(dto);
        return ResponseEntity.ok(savedAchievement);
    }

    @PutMapping("/achievements/{id}")
    public ResponseEntity<Achievement> updateAchievement(@PathVariable UUID id, @RequestBody AchievementDto dto) {
        Achievement updatedAchievement = masterDataService.updateAchievement(id, dto);
        return ResponseEntity.ok(updatedAchievement);
    }

    @DeleteMapping("/achievements/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable UUID id) {
        masterDataService.deleteAchievement(id);
        return ResponseEntity.ok().build();
    }

    // --- DAILY MISSIONS ---

    @GetMapping("/missions")
    public ResponseEntity<List<DailyMission>> getAllDailyMissions() {
        return ResponseEntity.ok(masterDataService.getAllDailyMissions());
    }

    @PostMapping("/missions")
    public ResponseEntity<DailyMission> addDailyMission(@RequestBody DailyMissionDto dto) {
        DailyMission savedMission = masterDataService.createDailyMission(dto);
        return ResponseEntity.ok(savedMission);
    }

    @PutMapping("/missions/{id}")
    public ResponseEntity<DailyMission> updateDailyMission(@PathVariable UUID id, @RequestBody DailyMissionDto dto) {
        DailyMission updatedMission = masterDataService.updateDailyMission(id, dto);
        return ResponseEntity.ok(updatedMission);
    }

    @DeleteMapping("/missions/{id}")
    public ResponseEntity<Void> deleteDailyMission(@PathVariable UUID id) {
        masterDataService.deleteDailyMission(id);
        return ResponseEntity.ok().build();
    }
}