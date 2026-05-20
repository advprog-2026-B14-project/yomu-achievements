package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    
    Optional<UserAchievement> findByUserIdAndAchievementId(String userId, UUID achievementId);
    
    List<UserAchievement> findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(String userId);
    
    List<UserAchievement> findByUserIdAndIsUnlockedTrue(String userId);

    /**
     * PERFORMANCE V2 (Optimized):
     * Fetches all unlocked UserAchievement records for a given list of userIds
     * in a SINGLE SQL query using JOIN FETCH to eagerly load the Achievement entity.
     *
     * This eliminates the N+1 problem: instead of 1 query to get the list
     * + N queries (one per user) to get their achievements, this does it all
     * in ONE round-trip to the database.
     *
     * JPQL JOIN FETCH forces an INNER JOIN at the SQL level, loading
     * `achievement` data alongside `user_achievement` in one result set.
     */
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.userId IN :userIds AND ua.isUnlocked = true")
    List<UserAchievement> findAllUnlockedByUserIds(List<String> userIds);
}