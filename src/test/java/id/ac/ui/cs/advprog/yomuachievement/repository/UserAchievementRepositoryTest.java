package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import id.ac.ui.cs.advprog.yomuachievement.model.UserAchievement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class UserAchievementRepositoryTest {
    @Autowired private UserAchievementRepository userAchievementRepository;
    @Autowired private AchievementRepository achievementRepository;

    @Test
    void testFindByUserIdAndAchievementId() {
        Achievement master = new Achievement();
        master.setNama("Test");
        master.setMilestoneType("test");
        master = achievementRepository.save(master);

        UserAchievement userRecord = new UserAchievement();
        userRecord.setUserId("user123");
        userRecord.setAchievement(master);
        userAchievementRepository.save(userRecord);

        Optional<UserAchievement> found = userAchievementRepository.findByUserIdAndAchievementId("user123", master.getId());
        assertTrue(found.isPresent());
        assertEquals("user123", found.get().getUserId());
    }
}