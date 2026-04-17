package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    
    Optional<UserAchievement> findByUserIdAndAchievementId(String userId, UUID achievementId);
}