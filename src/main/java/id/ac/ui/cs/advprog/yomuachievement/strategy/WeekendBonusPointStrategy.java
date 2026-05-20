package id.ac.ui.cs.advprog.yomuachievement.strategy;

import org.springframework.stereotype.Component;

/**
 * Strategy Pattern: WeekendBonusPointStrategy (Concrete Strategy 2)
 *
 * An alternative point calculation strategy: award double points on weekends
 * to incentivize learning outside of school days.
 *
 * This can be injected in place of StandardPointStrategy without ANY change
 * to the service that uses it. This is the power of the Strategy Pattern.
 */
@Component("weekendBonusPointStrategy")
public class WeekendBonusPointStrategy implements PointCalculationStrategy {

    private static final double WEEKEND_MULTIPLIER = 2.0;

    @Override
    public int calculate(int basePoints) {
        return (int) (basePoints * WEEKEND_MULTIPLIER);
    }
}
