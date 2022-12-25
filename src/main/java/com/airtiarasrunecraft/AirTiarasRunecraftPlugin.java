package com.airtiarasrunecraft;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.Skill.RUNECRAFT;

@PluginDescriptor(
        name = "Air Tiaras Runecraft"
)
@PluginDependency(XpTrackerPlugin.class)
@Slf4j
public class AirTiarasRunecraftPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private AirTiarasRunecraftConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AirTiarasRunecraftOverlay airTiarasRunecraftOverlay;

    @Getter
    @Setter
    private AirTiarasRunecraftSession session = new AirTiarasRunecraftSession();

    @Inject
    private XpTrackerService xpTrackerService;

    @Getter
    private int runecraftLevel;

    @Getter
    private boolean lapStarted;
    private Instant lapStartTime;
    private boolean isValidLap = false;

    private WorldPoint lastLocation = null;

	private WorldPoint startLocation = new WorldPoint(2933, 3288, 0);
    private WorldPoint stopLocation = new WorldPoint(2933, 3289, 0);

    @Provides
    AirTiarasRunecraftConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AirTiarasRunecraftConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        lastLocation = getPlayerLocation();
        resetLap();

        session = new AirTiarasRunecraftSession();

        //overlayManager.add(lapCounterOverlay);
        overlayManager.add(airTiarasRunecraftOverlay);
        runecraftLevel = client.getBoostedSkillLevel(RUNECRAFT);

        log.info("startUp");
    }

    @Override
    protected void shutDown() throws Exception {
        //overlayManager.remove(lapCounterOverlay);
        overlayManager.remove(airTiarasRunecraftOverlay);
        session = null;
        runecraftLevel = 0;
    }

    @Subscribe
    public void onGameTick(GameTick tick) {

        if(isOnStopTile() && lapStarted && !isValidLap) {
            // Stop the lap
            stopLap();
        } else if(isOnStartTile() && !lapStarted) {
            // Start a lap
            startLap();
        }

        //Register last location of the player
        lastLocation = getPlayerLocation();
        //log.debug(lastLocation.toString());
    }
    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        if (statChanged.getSkill() != RUNECRAFT)
        {
            return;
        }

        isValidLap = true;
    }

    private boolean isOnStartTile() {
        return startLocation.hashCode() == getPlayerLocation().hashCode();
    }

    private boolean isOnStopTile() {
        return stopLocation.hashCode() == getPlayerLocation().hashCode();
    }

    private WorldPoint getPlayerLocation() {
        Player local = client.getLocalPlayer();
        if (local == null) {
            return null;
        }

        WorldPoint location = local.getWorldLocation();

        return location;
    }
    private void startLap()
    {
        // Reset lap attributes
        log.info("Reset lap attributes");
        resetLap();

        // Start timer
        log.info("Start timer here");
        lapStartTime = Instant.now();

        lapStarted = true;
    }
    private void stopLap()
    {
        // End timer
        log.info("End timer here");
        // Register lap
        log.info("Push lap time to session");
        log.info("Increment lap count");
        session.incrementLapCount(client, xpTrackerService);

        // Clear lap data
        resetLap();
    }
    private void resetLap()
    {
        lapStarted = false;
        isValidLap = false;
    }
}
