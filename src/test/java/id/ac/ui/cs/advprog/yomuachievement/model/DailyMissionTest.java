package id.ac.ui.cs.advprog.yomuachievement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class DailyMissionTest {
    private DailyMission dailyMission;

    @BeforeEach
    void setUp() {
        dailyMission = new DailyMission();
        dailyMission.setId(UUID.randomUUID());
        dailyMission.setNamaMisi("Misi Test");
        dailyMission.setMilestoneTarget(10);
        dailyMission.setPoinReward(100);
    }

    @Test
    void testGettersAndSetters() {
        assertNotNull(dailyMission.getId());
        assertEquals("Misi Test", dailyMission.getNamaMisi());
        assertEquals(10, dailyMission.getMilestoneTarget());
        assertEquals(100, dailyMission.getPoinReward());
    }
}