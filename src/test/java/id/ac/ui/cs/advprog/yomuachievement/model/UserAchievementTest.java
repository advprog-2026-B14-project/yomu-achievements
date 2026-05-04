package id.ac.ui.cs.advprog.yomuachievement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserAchievementTest {
    private UserAchievement userAchievement;
    private Achievement achievement;

    @BeforeEach
    void setUp() {
        achievement = new Achievement();
        achievement.setId(UUID.randomUUID());

        userAchievement = new UserAchievement();
        userAchievement.setId(UUID.randomUUID());
        userAchievement.setUserId("user123");
        userAchievement.setAchievement(achievement);
        userAchievement.setTanggalDidapat(LocalDateTime.now());
        userAchievement.setIsPinned(true);
        userAchievement.setPinOrder(1);
        userAchievement.setCurrentProgress(5);
        userAchievement.setIsUnlocked(true);
    }

    @Test
    void testGettersAndSetters() {
        assertNotNull(userAchievement.getId());
        assertEquals("user123", userAchievement.getUserId());
        assertEquals(achievement, userAchievement.getAchievement());
        assertNotNull(userAchievement.getTanggalDidapat());
        assertTrue(userAchievement.getIsPinned());
        assertEquals(1, userAchievement.getPinOrder());
        assertEquals(5, userAchievement.getCurrentProgress());
        assertTrue(userAchievement.getIsUnlocked());
    }
}