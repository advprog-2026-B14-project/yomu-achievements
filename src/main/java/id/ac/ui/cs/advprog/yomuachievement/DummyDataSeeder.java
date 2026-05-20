package id.ac.ui.cs.advprog.yomuachievement;

import id.ac.ui.cs.advprog.yomuachievement.model.UserGamificationStat;
import id.ac.ui.cs.advprog.yomuachievement.repository.UserGamificationStatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DummyDataSeeder — seeds 1000 UserGamificationStat rows on startup.
 *
 * Only runs if the table is completely empty (idempotent).
 * Used to generate enough data to demonstrate the N+1 vs optimized query
 * performance difference in IntelliJ Profiler.
 *
 * After seeding, test with:
 *   GET http://localhost:8083/api/benchmark/clan-points/slow?userIds=user-1,user-2,...,user-1000
 *   GET http://localhost:8083/api/benchmark/clan-points/optimized?userIds=user-1,user-2,...,user-1000
 */
@Component
public class DummyDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DummyDataSeeder.class);
    private static final int SEED_COUNT = 1000;

    @Autowired
    private UserGamificationStatRepository userGamificationStatRepository;

    @Override
    public void run(String... args) {
        if (userGamificationStatRepository.count() == 0) {
            log.info("[DummyDataSeeder] Table is empty. Seeding {} UserGamificationStat records...", SEED_COUNT);

            Random random = new Random();
            List<UserGamificationStat> stats = new ArrayList<>(SEED_COUNT);

            for (int i = 1; i <= SEED_COUNT; i++) {
                UserGamificationStat stat = new UserGamificationStat();
                stat.setUserId("user-" + i);
                // Random points between 0 and 9999
                int points = random.nextInt(10000);
                stat.setTotalPoints(points);
                // Derive level from points (matches the addPoints() formula)
                stat.setLevel((points / 100) + 1);
                stats.add(stat);
            }

            // saveAll() uses a single transaction and batches inserts —
            // far more efficient than calling save() in a loop.
            userGamificationStatRepository.saveAll(stats);

            log.info("[DummyDataSeeder] Done! Seeded {} records into user_gamification_stats.", SEED_COUNT);
        } else {
            log.info("[DummyDataSeeder] Data already exists ({} rows). Skipping seed.",
                    userGamificationStatRepository.count());
        }
    }
}
