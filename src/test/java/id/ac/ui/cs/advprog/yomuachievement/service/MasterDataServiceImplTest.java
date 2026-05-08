package id.ac.ui.cs.advprog.yomuachievement.service;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.repository.AchievementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterDataServiceImplTest {

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private MasterDataServiceImpl masterDataService;

    @Test
    void testCreateAchievement() {
        // Arrange (Persiapan)
        Achievement achievement = new Achievement();
        achievement.setNama("Koyeb Master");
        achievement.setMilestoneType("deployment");

        
        when(achievementRepository.save(any(Achievement.class))).thenReturn(achievement);

        Achievement result = masterDataService.createAchievement(achievement);

        assertNotNull(result);
        assertEquals("Koyeb Master", result.getNama());
        
        verify(achievementRepository, times(1)).save(achievement); 
    }

    @Mock
    private id.ac.ui.cs.advprog.yomuachievement.repository.DailyMissionRepository dailyMissionRepository;

    @Test
    void testCreateDailyMission() {
        id.ac.ui.cs.advprog.yomuachievement.model.DailyMission mission = new id.ac.ui.cs.advprog.yomuachievement.model.DailyMission();
        mission.setNamaMisi("Misi Test");
        mission.setMilestoneTarget(1);

        when(dailyMissionRepository.save(any(id.ac.ui.cs.advprog.yomuachievement.model.DailyMission.class))).thenReturn(mission);

        id.ac.ui.cs.advprog.yomuachievement.model.DailyMission result = masterDataService.createDailyMission(mission);

        assertNotNull(result);
        assertEquals("Misi Test", result.getNamaMisi());
        verify(dailyMissionRepository, times(1)).save(mission);
    }
}