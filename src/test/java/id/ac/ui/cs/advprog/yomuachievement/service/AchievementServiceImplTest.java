package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomuachievement.model.*;
import id.ac.ui.cs.advprog.yomuachievement.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private DailyMissionRepository dailyMissionRepository;

    @Mock
    private UserDailyMissionRepository userDailyMissionRepository;

    @Mock
    private UserGamificationStatRepository userGamificationStatRepository;

    @InjectMocks
    private AchievementServiceImpl achievementService;

    @Test
    void testUpdateAchievementProgress() {
        UUID achievementId = UUID.randomUUID();
        String userId = "user-123";

        Achievement master = new Achievement();
        master.setId(achievementId);
        master.setMilestoneTarget(5);

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(master));
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)).thenReturn(Optional.empty());

        achievementService.updateAchievementProgress(userId, achievementId);

        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));
    }

    @Test
    void testUpdateMissionProgress() {
        UUID missionId = UUID.randomUUID();
        String userId = "user-123";

        DailyMission master = new DailyMission();
        master.setId(missionId);
        master.setMilestoneTarget(5);
        master.setPoinReward(50);

        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(master));
        when(userDailyMissionRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.empty());

        achievementService.updateMissionProgress(userId, missionId);

        verify(userDailyMissionRepository, times(1)).save(any(UserDailyMission.class));
    }

    @Test
    void testUpdateAchievementProgress_TriggerUnlock() {
        UUID achievementId = UUID.randomUUID();
        String userId = "user-123";

        Achievement master = new Achievement();
        master.setId(achievementId);
        master.setMilestoneTarget(5);
        master.setPoinReward(150);

        UserAchievement existingRecord = new UserAchievement();
        existingRecord.setUserId(userId);
        existingRecord.setAchievement(master);
        existingRecord.setCurrentProgress(4);
        existingRecord.setIsUnlocked(false);

        UserGamificationStat existingStat = new UserGamificationStat();
        existingStat.setUserId(userId);
        existingStat.setTotalPoints(50);
        existingStat.setLevel(1);

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(master));
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)).thenReturn(Optional.of(existingRecord));
        when(userGamificationStatRepository.findById(userId)).thenReturn(Optional.of(existingStat));

        achievementService.updateAchievementProgress(userId, achievementId);

        assertTrue(existingRecord.getIsUnlocked());
        assertNotNull(existingRecord.getTanggalDidapat());
        
        // Verify Stat Update: 50 + 150 = 200 pts. Level: (200/100) + 1 = 3
        assertEquals(200, existingStat.getTotalPoints());
        assertEquals(3, existingStat.getLevel());
        
        verify(userAchievementRepository).save(existingRecord);
        verify(userGamificationStatRepository).save(existingStat);
    }

    @Test
    void testUpdateMissionProgress_TriggerComplete() {
        UUID missionId = UUID.randomUUID();
        String userId = "user-123";

        DailyMission master = new DailyMission();
        master.setId(missionId);
        master.setMilestoneTarget(5);
        master.setPoinReward(75);

        UserDailyMission existingRecord = new UserDailyMission();
        existingRecord.setUserId(userId);
        existingRecord.setMission(master);
        existingRecord.setProgress(4);
        existingRecord.setIsCompleted(false);

        UserGamificationStat existingStat = new UserGamificationStat();
        existingStat.setUserId(userId);
        existingStat.setTotalPoints(20);
        existingStat.setLevel(1);

        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(master));
        when(userDailyMissionRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(existingRecord));
        when(userGamificationStatRepository.findById(userId)).thenReturn(Optional.of(existingStat));

        achievementService.updateMissionProgress(userId, missionId);

        assertTrue(existingRecord.getIsCompleted());
        assertNotNull(existingRecord.getTanggalSelesai());

        // Verify Stat Update: 20 + 75 = 95 pts. Level: (95/100) + 1 = 1
        assertEquals(95, existingStat.getTotalPoints());
        assertEquals(1, existingStat.getLevel());

        verify(userDailyMissionRepository).save(existingRecord);
        verify(userGamificationStatRepository).save(existingStat);
    }

    @Test
    void testUpdateAchievementProgress_TriggerUnlock_NewStat() {
        UUID achievementId = UUID.randomUUID();
        String userId = "new-user";

        Achievement master = new Achievement();
        master.setId(achievementId);
        master.setMilestoneTarget(1);
        master.setPoinReward(120);

        UserAchievement existingRecord = new UserAchievement();
        existingRecord.setUserId(userId);
        existingRecord.setAchievement(master);
        existingRecord.setCurrentProgress(0);
        existingRecord.setIsUnlocked(false);

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(master));
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)).thenReturn(Optional.of(existingRecord));
        when(userGamificationStatRepository.findById(userId)).thenReturn(Optional.empty());

        achievementService.updateAchievementProgress(userId, achievementId);

        ArgumentCaptor<UserGamificationStat> statCaptor = ArgumentCaptor.forClass(UserGamificationStat.class);
        verify(userGamificationStatRepository).save(statCaptor.capture());
        
        UserGamificationStat savedStat = statCaptor.getValue();
        assertEquals(userId, savedStat.getUserId());
        assertEquals(120, savedStat.getTotalPoints()); // 0 + 120
        assertEquals(2, savedStat.getLevel()); // (120/100) + 1 = 2
    }

    @Test
    void testUpdateAchievementProgress_NotFound() {
        UUID fakeId = UUID.randomUUID();
        String userId = "user-123";

        when(achievementRepository.findById(fakeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.updateAchievementProgress(userId, fakeId);
        });

        assertEquals("Achievement tidak ditemukan", exception.getMessage());
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void testUpdateMissionProgress_NotFound() {
        UUID fakeId = UUID.randomUUID();
        String userId = "user-123";

        when(dailyMissionRepository.findById(fakeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.updateMissionProgress(userId, fakeId);
        });

        assertEquals("Misi tidak ditemukan", exception.getMessage());
        verify(userDailyMissionRepository, never()).save(any());
    }

    @Test
    void testUpdateAchievementProgress_AlreadyUnlocked() {
        UUID achievementId = UUID.randomUUID();
        String userId = "user-123";

        Achievement master = new Achievement();
        master.setId(achievementId);
        master.setMilestoneTarget(5);

        UserAchievement existingRecord = new UserAchievement();
        existingRecord.setUserId(userId);
        existingRecord.setAchievement(master);
        existingRecord.setCurrentProgress(5);
        existingRecord.setIsUnlocked(true); 

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(master));
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)).thenReturn(Optional.of(existingRecord));

        achievementService.updateAchievementProgress(userId, achievementId);

        assertEquals(5, existingRecord.getCurrentProgress()); 
        verify(userAchievementRepository, never()).save(any());
        verify(userGamificationStatRepository, never()).save(any());
    }

    @Test
    void testUpdateMissionProgress_AlreadyCompleted() {
        UUID missionId = UUID.randomUUID();
        String userId = "user-123";

        DailyMission master = new DailyMission();
        master.setId(missionId);
        master.setMilestoneTarget(5);

        UserDailyMission existingRecord = new UserDailyMission();
        existingRecord.setUserId(userId);
        existingRecord.setMission(master);
        existingRecord.setProgress(5);
        existingRecord.setIsCompleted(true); 

        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(master));
        when(userDailyMissionRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(existingRecord));

        achievementService.updateMissionProgress(userId, missionId);

        assertEquals(5, existingRecord.getProgress()); 
        verify(userDailyMissionRepository, never()).save(any());
        verify(userGamificationStatRepository, never()).save(any());
    }

    @Test
    void testFindAllAchievements() {
        achievementService.findAllAchievements();
        verify(achievementRepository, times(1)).findAll();
    }

    @Test
    void testPinAchievement_Success() {
        String userId = "user-123";
        UUID achievementId = UUID.randomUUID();
        int pinOrder = 1;

        UserAchievement existingRecord = new UserAchievement();
        existingRecord.setUserId(userId);
        existingRecord.setIsUnlocked(true);

        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.of(existingRecord));

        achievementService.pinAchievement(userId, achievementId, pinOrder);

        assertTrue(existingRecord.getIsPinned());
        assertEquals(pinOrder, existingRecord.getPinOrder());
        verify(userAchievementRepository, times(1)).save(existingRecord);
    }

    @Test
    void testPinAchievement_NotUnlocked() {
        String userId = "user-123";
        UUID achievementId = UUID.randomUUID();

        UserAchievement existingRecord = new UserAchievement();
        existingRecord.setUserId(userId);
        existingRecord.setIsUnlocked(false);

        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.of(existingRecord));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.pinAchievement(userId, achievementId, 1);
        });

        assertEquals("Hanya achievement yang sudah terbuka yang bisa di-pin", exception.getMessage());
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void testPinAchievement_InvalidPinOrder() {
        String userId = "user-123";
        UUID achievementId = UUID.randomUUID();

        // Testing outside 1-3 range
        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.pinAchievement(userId, achievementId, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.pinAchievement(userId, achievementId, 4);
        });
        
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void testPinAchievement_NotFound() {
        String userId = "user-123";
        UUID achievementId = UUID.randomUUID();

        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.pinAchievement(userId, achievementId, 1);
        });

        assertEquals("Record UserAchievement tidak ditemukan", exception.getMessage());
    }

    @Test
    void testGetUserProfile_Success() {
        String userId = "user-123";
        UserGamificationStat stat = new UserGamificationStat();
        stat.setUserId(userId);
        stat.setTotalPoints(250);
        stat.setLevel(3);

        Achievement a1 = new Achievement();
        a1.setId(UUID.randomUUID());
        a1.setNama("Achievement 1");

        UserAchievement ua1 = new UserAchievement();
        ua1.setAchievement(a1);
        ua1.setPinOrder(1);
        ua1.setIsPinned(true);

        when(userGamificationStatRepository.findById(userId)).thenReturn(Optional.of(stat));
        when(userAchievementRepository.findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(userId))
                .thenReturn(List.of(ua1));

        UserProfileResponse profile = achievementService.getUserProfile(userId);

        assertEquals(userId, profile.getUserId());
        assertEquals(3, profile.getLevel());
        assertEquals(250, profile.getTotalPoints());
        assertEquals(1, profile.getPinnedAchievements().size());
        assertEquals("Achievement 1", profile.getPinnedAchievements().get(0).getNama());
    }

    @Test
    void testGetUserProfile_NoStat_DefaultValues() {
        String userId = "new-user";
        when(userGamificationStatRepository.findById(userId)).thenReturn(Optional.empty());
        when(userAchievementRepository.findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(userId))
                .thenReturn(new ArrayList<>());

        UserProfileResponse profile = achievementService.getUserProfile(userId);

        assertEquals(1, profile.getLevel());
        assertEquals(0, profile.getTotalPoints());
        assertTrue(profile.getPinnedAchievements().isEmpty());
    }
    @Test
    void testResetAllDailyMissions() {
        achievementService.resetAllDailyMissions();
        verify(userDailyMissionRepository, times(1)).deleteAll();
    }
}