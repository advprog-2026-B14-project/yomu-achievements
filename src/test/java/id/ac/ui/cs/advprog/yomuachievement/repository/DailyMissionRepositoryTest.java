package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.DailyMission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class DailyMissionRepositoryTest {
    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Test
    void testSaveAndFindDailyMission() {
        DailyMission mission = new DailyMission();
        mission.setNamaMisi("Login Harian");
        mission.setMilestoneTarget(1);
        mission.setPoinReward(10);
        DailyMission savedMission = dailyMissionRepository.save(mission);
        assertNotNull(savedMission.getId());
        assertEquals("Login Harian", savedMission.getNamaMisi());
    }
}