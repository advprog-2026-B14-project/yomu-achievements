package id.ac.ui.cs.advprog.yomuachievement.event;

import org.springframework.context.ApplicationEvent;
import java.util.UUID;

/**
 * Observer Pattern: MissionCompletedEvent (Spring Application Event)
 *
 * This is the "notification" object published to the Spring Event Bus.
 * It carries the data needed for any listener (observer) to react to
 * a mission completion. The publisher (AchievementServiceImpl) has
 * NO knowledge of who is listening. This is Loose Coupling in action.
 */
public class MissionCompletedEvent extends ApplicationEvent {

    private final String userId;
    private final UUID missionId;
    private final String missionName;

    public MissionCompletedEvent(Object source, String userId, UUID missionId, String missionName) {
        super(source);
        this.userId = userId;
        this.missionId = missionId;
        this.missionName = missionName;
    }

    public String getUserId() { return userId; }
    public UUID getMissionId() { return missionId; }
    public String getMissionName() { return missionName; }
}
