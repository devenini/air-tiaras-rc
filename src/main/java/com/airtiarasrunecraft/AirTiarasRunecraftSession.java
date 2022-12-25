package com.airtiarasrunecraft;

import com.google.common.collect.EvictingQueue;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
class AirTiarasRunecraftSession {
    private Instant lastLapCompleted;
    private int totalLaps;
    private int lapsTillGoal;
    private final EvictingQueue<Duration> lastLapTimes = EvictingQueue.create(30);
    private int lapsPerHour;

    void incrementLapCount(Client client, XpTrackerService xpTrackerService) {
        calculateLapsPerHour();

        ++totalLaps;

        final int currentExp = client.getSkillExperience(Skill.RUNECRAFT);
        final int goalXp = xpTrackerService.getEndGoalXp(Skill.RUNECRAFT);
        final int goalRemainingXp = goalXp - currentExp;
        double courseTotalExp = 650;

        lapsTillGoal = goalRemainingXp > 0 ? (int) Math.ceil(goalRemainingXp / courseTotalExp) : 0;
    }

    void calculateLapsPerHour() {
        Instant now = Instant.now();

        if (lastLapCompleted != null) {
            Duration timeSinceLastLap = Duration.between(lastLapCompleted, now);

            if (!timeSinceLastLap.isNegative()) {
                lastLapTimes.add(timeSinceLastLap);

                Duration sum = Duration.ZERO;
                for (Duration lapTime : lastLapTimes) {
                    sum = sum.plus(lapTime);
                }

                Duration averageLapTime = sum.dividedBy(lastLapTimes.size());
                lapsPerHour = (int) (Duration.ofHours(1).toMillis() / averageLapTime.toMillis());
            }
        }

        lastLapCompleted = now;
    }
    void resetLapCount() {
        totalLaps = 0;
        lapsTillGoal = 0;
        lastLapTimes.clear();
        lapsPerHour = 0;
    }
}