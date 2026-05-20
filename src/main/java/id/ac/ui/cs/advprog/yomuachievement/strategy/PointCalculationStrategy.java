package id.ac.ui.cs.advprog.yomuachievement.strategy;

/**
 * Strategy Pattern: PointCalculationStrategy
 *
 * This interface defines the contract for the point calculation algorithm.
 * By programming to this interface (Dependency Inversion Principle - SOLID),
 * we decouple the service from any specific reward calculation logic.
 * New strategies can be added without modifying existing code (Open/Closed Principle).
 */
public interface PointCalculationStrategy {

    /**
     * Calculates the final points to award a user based on the base reward.
     *
     * @param basePoints The raw poinReward value from the Achievement or DailyMission config.
     * @return The final integer points to add to the user's total.
     */
    int calculate(int basePoints);
}
