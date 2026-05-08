package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomuachievement.model.UserDailyMission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class UserDailyMissionRepositoryTest {
    @Autowired private UserDailyMissionRepository userDailyMissionRepository;
    @Autowired private DailyMissionRepository dailyMissionRepository;

    @Test
    void testFindByUserIdAndMissionId() {
        DailyMission master = new DailyMission();
        master.setNamaMisi("Test Misi");
        master.setMilestoneTarget(1);
        master.setPoinReward(10);
        master = dailyMissionRepository.save(master);

        UserDailyMission userRecord = new UserDailyMission();
        userRecord.setUserId("user123");
        userRecord.setMission(master);
        userDailyMissionRepository.save(userRecord);

        Optional<UserDailyMission> found = userDailyMissionRepository.findByUserIdAndMissionId("user123", master.getId());
        assertTrue(found.isPresent());
        assertEquals("user123", found.get().getUserId());
    }
}