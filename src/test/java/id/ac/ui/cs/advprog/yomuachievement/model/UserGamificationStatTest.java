package id.ac.ui.cs.advprog.yomuachievement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserGamificationStatTest {
    private UserGamificationStat stat;

    @BeforeEach
    void setUp() {
        stat = new UserGamificationStat();
        stat.setUserId("user123");
        stat.setTotalPoints(500);
        stat.setLevel(5);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("user123", stat.getUserId());
        assertEquals(500, stat.getTotalPoints());
        assertEquals(5, stat.getLevel());
    }
}