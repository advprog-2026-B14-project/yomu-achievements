package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.UserDailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDailyMissionRepository extends JpaRepository<UserDailyMission, UUID> {
    
    Optional<UserDailyMission> findByUserIdAndMissionId(String userId, UUID missionId);
}