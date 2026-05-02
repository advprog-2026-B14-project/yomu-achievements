package id.ac.ui.cs.advprog.yomuachievement.repository;

import id.ac.ui.cs.advprog.yomuachievement.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
}