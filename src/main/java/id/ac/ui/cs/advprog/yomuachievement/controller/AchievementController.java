package id.ac.ui.cs.advprog.yomuachievement.controller;

import id.ac.ui.cs.advprog.yomuachievement.dto.PinAchievementRequest;
import id.ac.ui.cs.advprog.yomuachievement.dto.PinnedAchievementDto;
import id.ac.ui.cs.advprog.yomuachievement.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    // Endpoint untuk update progres Achievement
    @PostMapping("/internal/progress/achievement")
    public ResponseEntity<String> updateAchievementProgress(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        UUID achievementId = UUID.fromString((String) payload.get("achievementId"));

        achievementService.updateAchievementProgress(userId, achievementId);
        return ResponseEntity.ok("Progres achievement berhasil diperbarui");
    }

    // Endpoint untuk update progres Misi Harian
    @PostMapping("/internal/progress/mission")
    public ResponseEntity<String> updateMissionProgress(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        UUID missionId = UUID.fromString((String) payload.get("missionId"));

        achievementService.updateMissionProgress(userId, missionId);
        return ResponseEntity.ok("Progres misi berhasil diperbarui");
    }

    // Endpoint untuk Pin Achievement
    @PostMapping("/achievements/pin")
    public ResponseEntity<String> pinAchievement(@RequestBody PinAchievementRequest request) {
        achievementService.pinAchievement(request.getUserId(), request.getAchievementId(), request.getPinOrder());
        return ResponseEntity.ok("Achievement berhasil di-pin");
    }

    // Endpoint untuk Get User Gamification Profile
    @GetMapping("/achievements/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(achievementService.getUserProfile(userId));
    }

    // Endpoint untuk Get All Master Achievements
    @GetMapping("/achievements/master")
    public ResponseEntity<List<Achievement>> getAllMasterAchievements() {
        return ResponseEntity.ok(achievementService.findAllAchievements());
    }

    // Endpoint untuk Get Unlocked Achievements (untuk dropdown pin profil)
    @GetMapping("/achievements/unlocked/{userId}")
    public ResponseEntity<List<PinnedAchievementDto>> getUnlockedAchievements(@PathVariable String userId) {
        return ResponseEntity.ok(achievementService.getUnlockedAchievements(userId));
    }

    // ─── Performance Benchmark Endpoints ────────────────────────────────────
    // Use these endpoints with IntelliJ Profiler or wrk/JMeter to compare V1 vs V2.
    // Pass a comma-separated list of userIds as a query param:
    //   GET /api/benchmark/clan-points/slow?userIds=user-1,user-2,user-3
    //   GET /api/benchmark/clan-points/optimized?userIds=user-1,user-2,user-3

    /**
     * PERFORMANCE V1: N+1 Query — fires 1 SELECT per userId in the list.
     * Expected: slow, latency grows linearly with clan size.
     */
    @GetMapping("/benchmark/clan-points/slow")
    public ResponseEntity<Map<String, Object>> benchmarkSlow(@RequestParam List<String> userIds) {
        long start = System.currentTimeMillis();
        int total = achievementService.calculateTotalClanPointsSlow(userIds);
        long elapsed = System.currentTimeMillis() - start;
        return ResponseEntity.ok(Map.of(
                "version", "V1 (N+1 — Slow)",
                "userCount", userIds.size(),
                "totalPoints", total,
                "elapsedMs", elapsed
        ));
    }

    /**
     * PERFORMANCE V2: Single aggregate query — 1 SELECT regardless of clan size.
     * Expected: fast, latency stays constant regardless of clan size.
     */
    @GetMapping("/benchmark/clan-points/optimized")
    public ResponseEntity<Map<String, Object>> benchmarkOptimized(@RequestParam List<String> userIds) {
        long start = System.currentTimeMillis();
        int total = achievementService.calculateTotalClanPointsOptimized(userIds);
        long elapsed = System.currentTimeMillis() - start;
        return ResponseEntity.ok(Map.of(
                "version", "V2 (Optimized — Single Query)",
                "userCount", userIds.size(),
                "totalPoints", total,
                "elapsedMs", elapsed
        ));
    }
}