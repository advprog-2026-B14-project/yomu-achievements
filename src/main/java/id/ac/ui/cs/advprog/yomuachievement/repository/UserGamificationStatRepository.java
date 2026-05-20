package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.UserGamificationStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGamificationStatRepository extends JpaRepository<UserGamificationStat, String> {

    /**
     * PERFORMANCE V2 (Optimized):
     * Fetches the total sum of points across ALL users in a given clan
     * (list of userIds) using a single SQL aggregate query.
     *
     * This replaces the N+1 loop in calculateTotalClanPointsSlow():
     *   - BEFORE: 1 query (get all userIds) + N queries (one per user to get stats) = N+1 DB calls
     *   - AFTER:  1 aggregate query = 1 DB call, regardless of clan size
     */
    @Query("SELECT COALESCE(SUM(s.totalPoints), 0) FROM UserGamificationStat s WHERE s.userId IN :userIds")
    Integer sumTotalPointsByUserIds(@Param("userIds") List<String> userIds);
}
