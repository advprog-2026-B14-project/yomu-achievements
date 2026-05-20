# yomu-achievements

## Component Diagram

Diagram berikut menunjukkan komponen-komponen internal dari microservice **Yomu Achievements** dan bagaimana mereka saling berinteraksi. Service ini bertanggung jawab atas fitur gamifikasi: Achievement, Daily Mission, Pinned Achievement, dan Leveling System.

```mermaid
flowchart TB
    Gateway["Yomu Gateway<br/>(API Gateway)"]
    Scheduler["Spring Scheduler<br/>(Cron Job Harian)"]

    subgraph AchievementService["Yomu Achievements Microservice"]
        direction TB

        subgraph Controllers["Controller Layer"]
            AC["AchievementController<br/>REST /api"]
            AMC["AdminMasterDataController<br/>REST /api/admin/master"]
        end

        subgraph Services["Service Layer"]
            AS["AchievementService<br/>(interface)"]
            ASI["AchievementServiceImpl<br/>(implementasi)"]
            MDS["MasterDataService<br/>(interface)"]
            MDSI["MasterDataServiceImpl<br/>(implementasi)"]
        end

        subgraph DTOs["DTO Layer"]
            PAR["PinAchievementRequest"]
            PAD["PinnedAchievementDto"]
            UPR["UserProfileResponse"]
        end

        subgraph Repositories["Repository Layer"]
            AR["AchievementRepository"]
            DR["DailyMissionRepository"]
            UAR["UserAchievementRepository"]
            UDMR["UserDailyMissionRepository"]
            UGSR["UserGamificationStatRepository"]
        end
    end

    DB[("PostgreSQL / Supabase")]

    Gateway -->|"REST / JSON"| AC
    Gateway -->|"REST / JSON"| AMC
    Scheduler -->|"@Scheduled cron"| ASI

    AC -->|"uses"| AS
    AS -.->|"implements"| ASI
    AMC -->|"uses"| MDS
    MDS -.->|"implements"| MDSI

    AC -->|"uses"| PAR
    AC -->|"uses"| UPR
    ASI -->|"uses"| PAD
    ASI -->|"uses"| UPR

    ASI -->|"queries"| AR
    ASI -->|"queries"| UAR
    ASI -->|"queries"| DR
    ASI -->|"queries"| UDMR
    ASI -->|"queries"| UGSR
    MDSI -->|"queries"| AR
    MDSI -->|"queries"| DR

    AR -->|"JPA"| DB
    DR -->|"JPA"| DB
    UAR -->|"JPA"| DB
    UDMR -->|"JPA"| DB
    UGSR -->|"JPA"| DB
```

### Penjelasan Komponen

| Komponen | Tanggung Jawab |
|---|---|
| **AchievementController** | Menerima request dari Gateway untuk update progres achievement/misi, pin achievement, dan mengambil profil gamifikasi user. |
| **AdminMasterDataController** | Menerima request dari admin untuk membuat data master (Achievement dan Daily Mission baru). |
| **AchievementServiceImpl** | Logika bisnis utama: tracking progres, distribusi poin, leveling, pin achievement, dan reset misi harian via cron job. |
| **MasterDataServiceImpl** | Logika bisnis sederhana untuk CRUD data master Achievement dan Daily Mission. |
| **Repository Layer** | 5 repository JPA yang masing-masing menangani persistence satu entity ke database PostgreSQL. |
| **DTO Layer** | Objek transfer data untuk request (`PinAchievementRequest`) dan response (`UserProfileResponse`, `PinnedAchievementDto`). |

---

## Code Diagram (Class Diagram)

### 1. Model / Entity Classes

```mermaid
classDiagram
    class Achievement {
        -UUID id
        -String nama
        -String deskripsi
        -Integer milestoneTarget
        -Integer poinReward
        -String milestoneType
        -String badgeUrl
    }

    class DailyMission {
        -UUID id
        -String namaMisi
        -Integer milestoneTarget
        -Integer poinReward
    }

    class UserAchievement {
        -UUID id
        -String userId
        -LocalDateTime tanggalDidapat
        -Boolean isPinned
        -Integer pinOrder
        -Integer currentProgress
        -Boolean isUnlocked
    }

    class UserDailyMission {
        -UUID id
        -String userId
        -Integer progress
        -Boolean isCompleted
        -LocalDateTime tanggalSelesai
    }

    class UserGamificationStat {
        -String userId
        -Integer totalPoints
        -Integer level
        +addPoints(int points) void
    }

    UserAchievement "*" --> "1" Achievement : achievement
    UserDailyMission "*" --> "1" DailyMission : mission
```

### 2. Service & Controller Classes

```mermaid
classDiagram
    class AchievementController {
        -AchievementService achievementService
        +updateAchievementProgress(payload) ResponseEntity
        +updateMissionProgress(payload) ResponseEntity
        +pinAchievement(request) ResponseEntity
        +getUserProfile(userId) ResponseEntity
    }

    class AdminMasterDataController {
        -MasterDataService masterDataService
        +addAchievement(achievement) ResponseEntity
        +addDailyMission(dailyMission) ResponseEntity
    }

    class AchievementService {
        <<interface>>
        +findAllAchievements() List~Achievement~
        +updateAchievementProgress(userId, achievementId) void
        +updateMissionProgress(userId, missionId) void
        +pinAchievement(userId, achievementId, pinOrder) void
        +getUserProfile(userId) UserProfileResponse
        +resetAllDailyMissions() void
    }

    class AchievementServiceImpl {
        -AchievementRepository achievementRepository
        -UserAchievementRepository userAchievementRepository
        -DailyMissionRepository dailyMissionRepository
        -UserDailyMissionRepository userDailyMissionRepository
        -UserGamificationStatRepository userGamificationStatRepository
        +findAllAchievements() List~Achievement~
        +updateAchievementProgress(userId, achievementId) void
        +updateMissionProgress(userId, missionId) void
        +pinAchievement(userId, achievementId, pinOrder) void
        +getUserProfile(userId) UserProfileResponse
        +resetAllDailyMissions() void
        -distributePointsAndLevelUp(userId, pointReward) void
    }

    class MasterDataService {
        <<interface>>
        +createAchievement(achievement) Achievement
        +createDailyMission(dailyMission) DailyMission
    }

    class MasterDataServiceImpl {
        -AchievementRepository achievementRepository
        -DailyMissionRepository dailyMissionRepository
        +createAchievement(achievement) Achievement
        +createDailyMission(dailyMission) DailyMission
    }

    AchievementController --> AchievementService : uses
    AdminMasterDataController --> MasterDataService : uses
    AchievementServiceImpl ..|> AchievementService : implements
    MasterDataServiceImpl ..|> MasterDataService : implements
```

### 3. Repository Interfaces

```mermaid
classDiagram
    class AchievementRepository {
        <<interface>>
    }

    class DailyMissionRepository {
        <<interface>>
    }

    class UserAchievementRepository {
        <<interface>>
        +findByUserIdAndAchievementId(userId, achievementId) Optional~UserAchievement~
        +findByUserIdAndIsPinnedTrueOrderByPinOrderAsc(userId) List~UserAchievement~
    }

    class UserDailyMissionRepository {
        <<interface>>
        +findByUserIdAndMissionId(userId, missionId) Optional~UserDailyMission~
    }

    class UserGamificationStatRepository {
        <<interface>>
    }

    class JpaRepository {
        <<interface>>
        +save(entity) T
        +findById(id) Optional~T~
        +findAll() List~T~
        +deleteAll() void
    }

    AchievementRepository --|> JpaRepository
    DailyMissionRepository --|> JpaRepository
    UserAchievementRepository --|> JpaRepository
    UserDailyMissionRepository --|> JpaRepository
    UserGamificationStatRepository --|> JpaRepository
```

### 4. DTO Classes

```mermaid
classDiagram
    class PinAchievementRequest {
        -String userId
        -UUID achievementId
        -Integer pinOrder
    }

    class PinnedAchievementDto {
        -UUID id
        -String nama
        -String deskripsi
        -Integer milestoneTarget
        -Integer poinReward
        -Integer pinOrder
    }

    class UserProfileResponse {
        -String userId
        -Integer level
        -Integer totalPoints
        -List~PinnedAchievementDto~ pinnedAchievements
    }

    UserProfileResponse --> PinnedAchievementDto : contains
```

---

## Performance & System Quality

This section documents the measurable performance improvements, load test results, and observability setup implemented in this microservice.

---

### 1. Performance Optimization: Solving the N+1 Query Problem

A common JPA anti-pattern is the **N+1 query problem**, where fetching a list of N records triggers N additional database queries — one per record. This was intentionally demonstrated and resolved in the `calculateTotalClanPoints` methods.

#### Root Cause
The naive implementation (V1) iterates over a list of `userId`s and calls `repository.findById(userId)` inside a loop. For a clan of 1,000 users, this produces **1,001 separate SELECT statements** to the database.

#### Solution
The optimized implementation (V2) replaces the loop entirely with a single JPQL aggregate query using `SUM(...) WHERE userId IN (...)`. This reduces **1,001 DB round-trips to exactly 1**, regardless of clan size.

```java
// V2: Single SQL aggregate — 1 DB call for any number of users
@Query("SELECT COALESCE(SUM(s.totalPoints), 0) FROM UserGamificationStat s WHERE s.userId IN :userIds")
Integer sumTotalPointsByUserIds(@Param("userIds") List<String> userIds);
```

#### Benchmark Results

| Metric | V1 (N+1 — Slow) | V2 (Optimized) | Improvement |
|---|---|---|---|
| Execution Time (1000 users) | **172.7 seconds** | **0.6 seconds** | **~99% faster** |
| DB Round-trips | 1,001 | 1 | -99.9% |
| Scalability | Linear (O(N)) | Constant (O(1)) | ✅ |

**V1 — N+1 Bottleneck (Slow):**

![N+1 Query Benchmark — Slow](assets/slow.png)

**V2 — Optimized Single Query (Fast):**

![Optimized Query Benchmark — Fast](assets/optimize.png)

---

### 2. Load Testing (k6)

A load test was conducted using [k6](https://k6.io/) to verify that the optimized endpoint can sustain high concurrent traffic without degrading.

#### Test Configuration

| Parameter | Value |
|---|---|
| Tool | k6 |
| Target Endpoint | `GET /api/benchmark/clan-points/optimized` |
| Virtual Users (VUs) | **100 concurrent VUs** |
| Duration | **30 seconds** (steady-state) |
| Threshold: P95 Latency | Must be **< 200ms** |
| Threshold: Error Rate | Must be **< 1%** |

#### Results

| Metric | Result | Threshold | Status |
|---|---|---|---|
| Total Requests | **3,325** | — | ✅ |
| Requests/sec | ~110 req/s | — | ✅ |
| P95 Response Time | < 200ms | < 200ms | ✅ PASSED |
| Error Rate | **0.00%** | < 1% | ✅ PASSED |

All configured thresholds were **met** — the optimized endpoint handles 100 concurrent users with zero errors and sub-200ms P95 latency.

**k6 Load Test Summary:**

![k6 Load Test Results](assets/grafanak6.png)

---

### 3. Observability — Sentry APM

[Sentry](https://sentry.io/) is integrated into this microservice for real-time **Application Performance Monitoring (APM)**, providing:

- **Transaction Tracing:** Every incoming HTTP request is captured as a Sentry performance transaction, showing a full flame graph of time spent in each layer (controller → service → repository → DB).
- **APDEX Score:** Automatically computed based on a configured satisfaction threshold (T = 500ms). Requests faster than T are *Satisfied*; between T and 4T are *Tolerating*; above 4T are *Frustrated*.
- **Slow Query Detection:** Any database query exceeding the `slow-request-threshold` (500ms) is automatically flagged and attached to its parent transaction in the Sentry dashboard.
- **100% Sampling:** During development, `traces-sample-rate=1.0` captures every transaction. This can be lowered to `0.2` in production to reduce data volume.

**Sentry APM Dashboard:**

![Sentry APM Dashboard](assets/sentry.png)

#### Configuration

Sentry is activated by setting `SENTRY_DSN` in your `.env` file:

```properties
# application.properties
sentry.dsn=${SENTRY_DSN}
sentry.traces-sample-rate=1.0
sentry.enable-db-query-tracing=true
sentry.slow-request-threshold=500
```

```env
# .env
SENTRY_DSN=https://<your-key>@o<org-id>.ingest.sentry.io/<project-id>
```
