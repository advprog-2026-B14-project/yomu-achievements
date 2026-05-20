package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.dto.PinnedAchievementDto;
import id.ac.ui.cs.advprog.yomuachievement.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomuachievement.event.MissionCompletedEvent;
import id.ac.ui.cs.advprog.yomuachievement.model.*;
import id.ac.ui.cs.advprog.yomuachievement.repository.*;
import id.ac.ui.cs.advprog.yomuachievement.strategy.PointCalculationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Main service implementation for the Achievements & Gamification module.
 *
 * This class demonstrates three design patterns working together:
 *   1. BUILDER PATTERN    — All DTO construction uses the Lombok @Builder pattern.
 *   2. STRATEGY PATTERN   — Point calculation is delegated to a PointCalculationStrategy.
 *   3. OBSERVER PATTERN   — Mission completion fires a Spring ApplicationEvent (loose coupling).
 */
@Service
public class AchievementServiceImpl implements AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Autowired
    private UserDailyMissionRepository userDailyMissionRepository;

    @Autowired
    private UserGamificationStatRepository userGamificationStatRepository;

    // --- Strategy Pattern: Inject both strategies; choose dynamically at runtime ---
    @Autowired
    @Qualifier("standardPointStrategy")
    private PointCalculationStrategy standardStrategy;

    @Autowired
    @Qualifier("weekendBonusPointStrategy")
    private PointCalculationStrategy weekendBonusStrategy;

    // --- Observer Pattern: Publisher for Spring Application Events ---
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public List<Achievement> findAllAchievements() {
        return achievementRepository.findAll();
    }

    @Override
    public void updateAchievementProgress(String userId, UUID achievementId) {
        Achievement master = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement tidak ditemukan"));

        UserAchievement userRecord = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)
                .orElseGet(() -> {
                    UserAchievement newRecord = new UserAchievement();
                    newRecord.setUserId(userId);
                    newRecord.setAchievement(master);
                    newRecord.setCurrentProgress(0);
                    newRecord.setIsUnlocked(false);
                    return newRecord;
                });

        if (!userRecord.getIsUnlocked()) {
            userRecord.setCurrentProgress(userRecord.getCurrentProgress() + 1);

            if (userRecord.getCurrentProgress() >= master.getMilestoneTarget()) {
                userRecord.setIsUnlocked(true);
                userRecord.setTanggalDidapat(LocalDateTime.now());

                // Strategy Pattern: select correct strategy at runtime
                distributePointsAndLevelUp(userId, master.getPoinReward());
            }
            userAchievementRepository.save(userRecord);
        }
    }

    @Override
    public void updateMissionProgress(String userId, UUID missionId) {
        DailyMission master = dailyMissionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Misi tidak ditemukan"));

        UserDailyMission userRecord = userDailyMissionRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseGet(() -> {
                    UserDailyMission newRecord = new UserDailyMission();
                    newRecord.setUserId(userId);
                    newRecord.setMission(master);
                    newRecord.setProgress(0);
                    newRecord.setIsCompleted(false);
                    return newRecord;
                });

        if (!userRecord.getIsCompleted()) {
            userRecord.setProgress(userRecord.getProgress() + 1);

            if (userRecord.getProgress() >= master.getMilestoneTarget()) {
                userRecord.setIsCompleted(true);
                userRecord.setTanggalSelesai(LocalDateTime.now());

                // Strategy Pattern: select correct strategy at runtime
                distributePointsAndLevelUp(userId, master.getPoinReward());

                // Observer Pattern: publish the event — the service does NOT directly
                // call the Social Module. It just fires an event and forgets.
                eventPublisher.publishEvent(new MissionCompletedEvent(
                        this,
                        userId,
                        missionId,
                        master.getNamaMisi()
                ));
            }
            userDailyMissionRepository.save(userRecord);
        }
    }

    @Override
    public void pinAchievement(String userId, UUID achievementId, Integer pinOrder) {
        if (pinOrder == null || pinOrder < 1 || pinOrder > 3) {
            throw new IllegalArgumentException("Pin order harus antara 1 dan 3");
        }

        UserAchievement userRecord = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow(() -> new RuntimeException("Record UserAchievement tidak ditemukan"));

        if (!userRecord.getIsUnlocked()) {
            throw new RuntimeException("Hanya achievement yang sudah terbuka yang bisa di-pin");
        }

        Optional<UserAchievement> existingPinned = userAchievementRepository
            .findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(userId).stream()
            .filter(ua -> ua.getPinOrder().equals(pinOrder))
            .findFirst();

        if (existingPinned.isPresent()) {
            UserAchievement oldPinned = existingPinned.get();
            oldPinned.setIsPinned(false);
            oldPinned.setPinOrder(null);
            userAchievementRepository.save(oldPinned);
        }

        userRecord.setIsPinned(true);
        userRecord.setPinOrder(pinOrder);
        userAchievementRepository.save(userRecord);
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        UserGamificationStat stat = userGamificationStatRepository.findById(userId)
                .orElseGet(() -> {
                    UserGamificationStat newStat = new UserGamificationStat();
                    newStat.setUserId(userId);
                    newStat.setTotalPoints(0);
                    newStat.setLevel(1);
                    return newStat;
                });

        List<UserAchievement> pinned = userAchievementRepository.findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(userId);

        // Builder Pattern: construct each DTO using the Lombok builder — explicit, readable,
        // and safe (no positional constructor args that can silently be in wrong order).
        List<PinnedAchievementDto> pinnedDtos = pinned.stream()
                .map(ua -> PinnedAchievementDto.builder()
                        .id(ua.getAchievement().getId())
                        .nama(ua.getAchievement().getNama())
                        .deskripsi(ua.getAchievement().getDeskripsi())
                        .milestoneTarget(ua.getAchievement().getMilestoneTarget())
                        .poinReward(ua.getAchievement().getPoinReward())
                        .pinOrder(ua.getPinOrder())
                        .build())
                .collect(Collectors.toList());

        // Builder Pattern: construct the top-level response DTO
        return UserProfileResponse.builder()
                .userId(userId)
                .level(stat.getLevel())
                .totalPoints(stat.getTotalPoints())
                .pinnedAchievements(pinnedDtos)
                .build();
    }

    @Override
    public List<PinnedAchievementDto> getUnlockedAchievements(String userId) {
        List<UserAchievement> unlocked = userAchievementRepository.findByUserIdAndIsUnlockedTrue(userId);

        // Builder Pattern: consistent, type-safe DTO construction
        return unlocked.stream()
                .map(ua -> PinnedAchievementDto.builder()
                        .id(ua.getAchievement().getId())
                        .nama(ua.getAchievement().getNama())
                        .deskripsi(ua.getAchievement().getDeskripsi())
                        .milestoneTarget(ua.getAchievement().getMilestoneTarget())
                        .poinReward(ua.getAchievement().getPoinReward())
                        .pinOrder(ua.getPinOrder())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ========================================================================
     * PERFORMANCE V1: N+1 Query Problem (SLOW)
     * ========================================================================
     * Scenario: calculate the total combined points of a "clan" of users.
     *
     * This method first fetches all stat records with findAll() (Query #1),
     * then filters in Java, which in a real app would be replaced by iterating
     * a list of clanUserIds and calling findById() for EACH user — triggering
     * N additional SELECT queries (one per user).
     *
     * SQL executed: 1 (get all stats) + N (one per userId in clanUserIds)
     * Total DB round-trips: N + 1
     * ========================================================================
     */
    @Override
    public int calculateTotalClanPointsSlow(List<String> clanUserIds) {
        int totalPoints = 0;

        // N+1 PROBLEM: for each userId in the clan, we make a SEPARATE DB call.
        // With 100 users → 100 individual SELECT queries to user_gamification_stats.
        for (String userId : clanUserIds) {
            // ↓ This fires: SELECT * FROM user_gamification_stats WHERE user_id = ?
            // one separate round-trip per user!
            UserGamificationStat stat = userGamificationStatRepository.findById(userId)
                    .orElse(null);

            if (stat != null) {
                totalPoints += stat.getTotalPoints();
            }
        }

        return totalPoints;
    }

    /**
     * ========================================================================
     * PERFORMANCE V2: Single Optimized Aggregate Query (FAST)
     * ========================================================================
     * Solves the N+1 problem by delegating the aggregation to the DATABASE.
     * The JPQL query in UserGamificationStatRepository executes:
     *
     *   SELECT COALESCE(SUM(s.total_points), 0)
     *   FROM user_gamification_stats s
     *   WHERE s.user_id IN (?, ?, ?, ...)
     *
     * SQL executed: 1 query with IN clause, regardless of clan size.
     * Total DB round-trips: 1
     *
     * Expected speedup: >50% for clans of even 10+ users, and scales
     * proportionally — 100 users → ~100x fewer DB calls.
     * ========================================================================
     */
    @Override
    public int calculateTotalClanPointsOptimized(List<String> clanUserIds) {
        // Single DB round-trip: SELECT SUM(...) WHERE user_id IN (...)
        Integer total = userGamificationStatRepository.sumTotalPointsByUserIds(clanUserIds);
        return total != null ? total : 0;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetAllDailyMissions() {
        userDailyMissionRepository.deleteAll();
    }

    /**
     * Helper method that selects the correct PointCalculationStrategy at runtime,
     * then delegates the actual calculation to it.
     *
     * STRATEGY PATTERN:
     * - On weekends (Saturday/Sunday) → WeekendBonusPointStrategy (2x points).
     * - On weekdays → StandardPointStrategy (1x points).
     *
     * This replaces what was previously a hardcoded `stat.addPoints(pointReward)` call.
     * New strategies (e.g., HolidayBonusStrategy) can be added without touching this class.
     */
    private void distributePointsAndLevelUp(String userId, Integer basePointReward) {
        if (basePointReward == null) basePointReward = 0;

        // Select strategy based on the current day of the week
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        PointCalculationStrategy strategy = (today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY)
                ? weekendBonusStrategy
                : standardStrategy;

        // Delegate calculation to the selected strategy
        int finalPoints = strategy.calculate(basePointReward);

        UserGamificationStat stat = userGamificationStatRepository.findById(userId)
                .orElseGet(() -> {
                    UserGamificationStat newStat = new UserGamificationStat();
                    newStat.setUserId(userId);
                    return newStat;
                });

        stat.addPoints(finalPoints);
        userGamificationStatRepository.save(stat);
    }
}