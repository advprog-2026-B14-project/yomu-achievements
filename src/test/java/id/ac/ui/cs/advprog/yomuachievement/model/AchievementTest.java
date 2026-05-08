package id.ac.ui.cs.advprog.yomuachievement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AchievementTest {

    private Achievement achievement;

    @BeforeEach
    void setUp() {
        this.achievement = new Achievement();
        this.achievement.setNama("Penakluk Bug");
        this.achievement.setDeskripsi("Berhasil memperbaiki 10 bug berturut-turut");
        this.achievement.setMilestoneTarget(10);
        this.achievement.setPoinReward(50);
        this.achievement.setMilestoneType("debugging");
        this.achievement.setBadgeUrl("https://example.com/badge.png");
    }

    @Test
    void testGetMilestoneTargetShouldReturnCorrectValue() {
        assertEquals(10, this.achievement.getMilestoneTarget());
    }

    @Test
    void testGetNamaShouldReturnCorrectValue() {
        assertEquals("Penakluk Bug", this.achievement.getNama());
    }
    
    @Test
    void testDefaultValues() {
        Achievement emptyAchievement = new Achievement();
        assertEquals(0, emptyAchievement.getMilestoneTarget());
        assertEquals(0, emptyAchievement.getPoinReward());
    }
}