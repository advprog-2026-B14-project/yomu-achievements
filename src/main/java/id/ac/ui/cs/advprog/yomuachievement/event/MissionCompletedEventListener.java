package id.ac.ui.cs.advprog.yomuachievement.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: MissionCompletedEventListener
 *
 * This is a concrete "Observer". It is completely decoupled from the publisher
 * (AchievementServiceImpl). Spring's event bus is the "Subject". When a
 * MissionCompletedEvent is published, this listener reacts automatically.
 *
 * To integrate with the Social Module, the social module can register its own
 * separate @EventListener in its own Spring context — the achievements module
 * does NOT need to be changed AT ALL. This is the Open/Closed Principle.
 */
@Component
public class MissionCompletedEventListener {

    private static final Logger log = LoggerFactory.getLogger(MissionCompletedEventListener.class);

    /**
     * Reacts when a user completes a daily mission.
     * Currently logs the event. This can be extended to send a Webhook,
     * call a Feign Client to the Social Module, or publish a Kafka message.
     */
    @EventListener
    public void onMissionCompleted(MissionCompletedEvent event) {
        log.info("[OBSERVER] Mission Completed Event received! userId={}, missionId={}, missionName='{}'",
                event.getUserId(),
                event.getMissionId(),
                event.getMissionName()
        );

        // TODO: In a real microservice, this is where you call the Social Module:
        // socialModuleClient.notifyMissionCompleted(event.getUserId(), event.getMissionName());
    }
}
