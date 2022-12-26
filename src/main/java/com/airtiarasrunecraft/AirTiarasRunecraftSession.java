package com.airtiarasrunecraft;

import com.google.common.collect.EvictingQueue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Slf4j
class AirTiarasRunecraftSession {
    private Instant lastLapCompleted;
    private int totalLaps;
    private int lapsTillGoal;

    void incrementLapCount(Client client, XpTrackerService xpTrackerService) {
        ++totalLaps;

        calculateLapsUntilGoal(client, xpTrackerService);
    }

    void calculateLapsUntilGoal(Client client, XpTrackerService xpTrackerService)
    {
        final int currentExp = client.getSkillExperience(Skill.RUNECRAFT);
        final int rcLvl = client.getBoostedSkillLevel(Skill.RUNECRAFT);
        int goalXp = xpTrackerService.getEndGoalXp(Skill.RUNECRAFT);

        // Set goal as next level
        if (goalXp == 0) {
            goalXp = Experience.getXpForLevel(rcLvl + 1);
        }

        final int goalRemainingXp = goalXp - currentExp;
        double courseTotalExp = 650; //hardcoded 26 air tiaras xp lap

        lapsTillGoal = goalRemainingXp > 0 ? (int) Math.ceil(goalRemainingXp / courseTotalExp) : 0;
    }

    void resetLapCount() {
        totalLaps = 0;
        lapsTillGoal = 0;
    }
}