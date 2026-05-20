package id.ac.ui.cs.advprog.yomuachievement.strategy;

import org.springframework.stereotype.Component;

/**
 * Strategy Pattern: StandardPointStrategy (Concrete Strategy 1)
 *
 * The default point calculation: award the base points as-is.
 * This is the strategy used on normal (non-weekend) days.
 */
@Component("standardPointStrategy")
public class StandardPointStrategy implements PointCalculationStrategy {

    @Override
    public int calculate(int basePoints) {
        return basePoints;
    }
}
