package id.ac.ui.cs.advprog.yomuachievement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserDailyMissionTest {
    private UserDailyMission userDailyMission;
    private DailyMission mission;

    @BeforeEach
    void setUp() {
        mission = new DailyMission();
        mission.setId(UUID.randomUUID());

        userDailyMission = new UserDailyMission();
        userDailyMission.setId(UUID.randomUUID());
        userDailyMission.setUserId("user123");
        userDailyMission.setMission(mission);
        userDailyMission.setProgress(5);
        userDailyMission.setIsCompleted(true);
        userDailyMission.setTanggalSelesai(LocalDateTime.now());
    }

    @Test
    void testGettersAndSetters() {
        assertNotNull(userDailyMission.getId());
        assertEquals("user123", userDailyMission.getUserId());
        assertEquals(mission, userDailyMission.getMission());
        assertEquals(5, userDailyMission.getProgress());
        assertTrue(userDailyMission.getIsCompleted());
        assertNotNull(userDailyMission.getTanggalSelesai());
    }
}