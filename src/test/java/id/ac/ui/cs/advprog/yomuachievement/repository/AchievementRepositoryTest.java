package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AchievementRepositoryTest {

    @Autowired
    private AchievementRepository achievementRepository;

    @Test
    void testSaveAndFindAchievement() {
        Achievement achievement = new Achievement();
        achievement.setNama("Raja Push Commit");
        achievement.setMilestoneType("git");
        achievement.setMilestoneTarget(100);

        Achievement savedAchievement = achievementRepository.save(achievement);

        assertNotNull(savedAchievement.getId()); // ID harus otomatis ter-generate (UUID)
        assertEquals("Raja Push Commit", savedAchievement.getNama());
    }
}