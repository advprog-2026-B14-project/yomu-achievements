package id.ac.ui.cs.advprog.yomuachievement.controller;

import id.ac.ui.cs.advprog.yomuachievement.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal") // Kita gunakan /internal sesuai rencana milestone kamu
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    // Endpoint untuk update progres Achievement
    @PostMapping("/progress/achievement")
    public ResponseEntity<String> updateAchievementProgress(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        UUID achievementId = UUID.fromString((String) payload.get("achievementId"));

        achievementService.updateAchievementProgress(userId, achievementId);
        return ResponseEntity.ok("Progres achievement berhasil diperbarui");
    }

    // Endpoint untuk update progres Misi Harian
    @PostMapping("/progress/mission")
    public ResponseEntity<String> updateMissionProgress(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        UUID missionId = UUID.fromString((String) payload.get("missionId"));

        achievementService.updateMissionProgress(userId, missionId);
        return ResponseEntity.ok("Progres misi berhasil diperbarui");
    }
}