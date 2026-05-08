package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.*;
import id.ac.ui.cs.advprog.yomuachievement.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        UserAchievement existingRecord = new UserAchievement();
        existingRecord.setUserId(userId);
        existingRecord.setAchievement(master);
        existingRecord.setCurrentProgress(4); // Kurang 1 untuk unlock
        existingRecord.setIsUnlocked(false);

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(master));
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId)).thenReturn(Optional.of(existingRecord));

        achievementService.updateAchievementProgress(userId, achievementId);

        assertTrue(existingRecord.getIsUnlocked()); // Pastikan berubah jadi true
        assertNotNull(existingRecord.getTanggalDidapat());
        verify(userAchievementRepository, times(1)).save(existingRecord);
    }

    @Test
    void testUpdateMissionProgress_TriggerComplete() {
        UUID missionId = UUID.randomUUID();
        String userId = "user-123";

        DailyMission master = new DailyMission();
        master.setId(missionId);
        master.setMilestoneTarget(5);

        UserDailyMission existingRecord = new UserDailyMission();
        existingRecord.setUserId(userId);
        existingRecord.setMission(master);
        existingRecord.setProgress(4); // Kurang 1 untuk complete
        existingRecord.setIsCompleted(false);

        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(master));
        when(userDailyMissionRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(existingRecord));

        achievementService.updateMissionProgress(userId, missionId);

        assertTrue(existingRecord.getIsCompleted()); // Pastikan berubah jadi true
        assertNotNull(existingRecord.getTanggalSelesai());
        verify(userDailyMissionRepository, times(1)).save(existingRecord);
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
    }

    @Test
    void testFindAllAchievements() {
        achievementService.findAllAchievements();
        verify(achievementRepository, times(1)).findAll();
    }
}