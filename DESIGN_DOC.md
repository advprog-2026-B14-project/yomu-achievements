# Software Design Document
### Module: `yomu-achievements` — Achievements & Gamification Microservice
### Project: Yomu E-Learning Platform

---

## 1. Overview

This document details the deliberate software design decisions made in the `yomu-achievements` microservice, focusing on **3 implemented Design Patterns** and adherence to the **SOLID Principles**. Each pattern is explained with a *Before* (problematic) and *After* (refactored) comparison to demonstrate the engineering rationale.

---

## 2. Design Pattern 1: Builder Pattern

### Problem (Before)
When constructing DTOs like `UserProfileResponse` and `PinnedAchievementDto`, the only option was to use a positional all-args constructor:

```java
// FRAGILE: easy to swap args silently, unreadable
PinnedAchievementDto dto = new PinnedAchievementDto(
    ua.getAchievement().getId(),
    ua.getAchievement().getNama(),
    ua.getAchievement().getDeskripsi(),
    ua.getAchievement().getMilestoneTarget(),
    ua.getAchievement().getPoinReward(),
    ua.getPinOrder()
);
```

**Issues:** Fragile, unreadable, violates OCP (adding a field breaks all callers).

### Solution (After)
All DTOs are annotated with Lombok `@Builder`. Construction is self-documenting and order-independent:

```java
// BUILDER PATTERN: safe, explicit, readable
PinnedAchievementDto dto = PinnedAchievementDto.builder()
    .id(ua.getAchievement().getId())
    .nama(ua.getAchievement().getNama())
    .deskripsi(ua.getAchievement().getDeskripsi())
    .milestoneTarget(ua.getAchievement().getMilestoneTarget())
    .poinReward(ua.getAchievement().getPoinReward())
    .pinOrder(ua.getPinOrder())
    .build();
```

**Files Modified:** `dto/PinnedAchievementDto.java`, `dto/UserProfileResponse.java`, `dto/AchievementDto.java`, `dto/DailyMissionDto.java`, `service/AchievementServiceImpl.java`

**SOLID:** SRP (DTO holds data, builder handles construction). OCP (new optional fields do not break callers).

---

## 3. Design Pattern 2: Strategy Pattern

### Problem (Before)
The `distributePointsAndLevelUp` method had **hardcoded reward logic**:

```java
// BEFORE: Hardcoded, inflexible. Every new event requires editing this method.
private void distributePointsAndLevelUp(String userId, Integer pointReward) {
    stat.addPoints(pointReward); // <-- fixed, cannot be swapped
}
```

**Issues:** Violates OCP and SRP. Logic is untestable in isolation.

### Solution (After)
A `PointCalculationStrategy` interface was created with two concrete implementations:

```
strategy/
├── PointCalculationStrategy.java    (Interface)
├── StandardPointStrategy.java       (1x points — weekdays)
└── WeekendBonusPointStrategy.java   (2x points — weekends)
```

The service selects the correct strategy at runtime:

```java
// AFTER: Open/Closed. New strategy = new class, zero changes here.
DayOfWeek today = LocalDate.now().getDayOfWeek();
PointCalculationStrategy strategy =
    (today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY)
        ? weekendBonusStrategy
        : standardStrategy;

int finalPoints = strategy.calculate(basePoints);
```

**SOLID:** OCP (new strategies = new classes, no modification). SRP (calculation logic isolated). DIP (service depends on abstraction `PointCalculationStrategy`, not on concrete classes).

---

## 4. Design Pattern 3: Observer Pattern (Spring Application Events)

### Problem (Before)
When a mission was completed, a direct call to the Social Module created tight coupling:

```java
// BEFORE: Tight coupling. Social Module down = core logic fails.
// Adding a new consumer requires editing this core method.
socialModuleClient.notifyMissionCompleted(userId, missionName);
```

**Issues:** Violates OCP, SRP. Tightly coupled to Social Module.

### Solution (After)
Spring's `ApplicationEventPublisher` acts as the Subject (Observable). The service publishes and forgets. Listeners (Observers) react independently:

```
event/
├── MissionCompletedEvent.java           (The Event — data carrier)
└── MissionCompletedEventListener.java   (Concrete Observer)
```

**Publisher (AchievementServiceImpl) — fires and forgets:**
```java
// No knowledge of who is listening. Zero coupling.
eventPublisher.publishEvent(new MissionCompletedEvent(this, userId, missionId, missionName));
```

**Listener (completely decoupled):**
```java
@Component
public class MissionCompletedEventListener {
    @EventListener
    public void onMissionCompleted(MissionCompletedEvent event) {
        // Notify Social Module here — AchievementServiceImpl is untouched
        log.info("Mission completed! User={}, Mission={}", event.getUserId(), event.getMissionName());
    }
}
```

Adding a Leaderboard listener requires **zero changes** to `AchievementServiceImpl`.

**SOLID:** OCP (new listeners = new classes). DIP (service depends on `ApplicationEventPublisher` abstraction). ISP (each listener handles only what it needs).

---

## 5. SOLID Principles Summary Table

| Principle | Implementation |
|---|---|
| **S** — Single Responsibility | `AchievementServiceImpl` orchestrates; `PointCalculationStrategy` calculates; Listener handles notifications |
| **O** — Open/Closed | New point strategy or new event listener = new class, no modification of existing code |
| **L** — Liskov Substitution | `WeekendBonusPointStrategy` fully substitutes `StandardPointStrategy` via the interface |
| **I** — Interface Segregation | `AchievementService` and `MasterDataService` are separate; `PointCalculationStrategy` has exactly 1 method |
| **D** — Dependency Inversion | Service depends on `PointCalculationStrategy` and `ApplicationEventPublisher` (abstractions only) |

---

## 6. Final Package Structure

```
src/main/java/.../yomuachievement/
├── controller/
│   ├── AchievementController.java
│   └── AdminMasterDataController.java
├── dto/
│   ├── AchievementDto.java              # @Builder — Pattern 1
│   ├── DailyMissionDto.java             # @Builder — Pattern 1
│   ├── PinnedAchievementDto.java        # @Builder — Pattern 1
│   ├── PinAchievementRequest.java
│   └── UserProfileResponse.java         # @Builder — Pattern 1
├── event/
│   ├── MissionCompletedEvent.java       # Observer Event — Pattern 3
│   └── MissionCompletedEventListener.java # Observer Listener — Pattern 3
├── model/
├── repository/
├── service/
│   ├── AchievementService.java
│   ├── AchievementServiceImpl.java      # Wires all 3 patterns
│   ├── MasterDataService.java
│   └── MasterDataServiceImpl.java
└── strategy/
    ├── PointCalculationStrategy.java    # Strategy Interface — Pattern 2
    ├── StandardPointStrategy.java       # Concrete Strategy — Pattern 2
    └── WeekendBonusPointStrategy.java   # Concrete Strategy — Pattern 2
```
