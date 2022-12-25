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

import java.util.Objects;

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
    private boolean lapStarted;
    @Getter
    @Setter
    private Stopwatch stopWatch;
    private boolean isValidLap = false;
    //@todo : Only allow to start laps on free to play world. Lap should reset if hopping in p2p
    private final boolean allowedToStartLap = false;
    private WorldPoint lastLocation = null;
    private final WorldPoint startLocation = new WorldPoint(2933, 3288, 0);
    private final WorldPoint stopLocation = new WorldPoint(2933, 3289, 0);

    public AirTiarasRunecraftPlugin() {
    }

    @Provides
    AirTiarasRunecraftConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AirTiarasRunecraftConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        lastLocation = getPlayerLocation();
        resetLap();

        session = new AirTiarasRunecraftSession();
        stopWatch = new Stopwatch("Stopwatch");

        overlayManager.add(airTiarasRunecraftOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(airTiarasRunecraftOverlay);
        session = null;
        stopWatch = null;
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        try {
            if (isOnStopTile() && lapStarted && isValidLap) {
                // Stop the lap
                stopLap();
            } else if (isOnStartTile() && !lapStarted && Objects.requireNonNull(lastLocation).hashCode() == Objects.requireNonNull(stopLocation).hashCode()) {
                // Start a lap
                startLap();
            }

            //Register last location tile of the player
            if (lastLocation != getPlayerLocation()) {
                lastLocation = getPlayerLocation();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (statChanged.getSkill() != RUNECRAFT) {
            return;
        }

        //Making sure a player earned Runecraft xp to make the world count
        //@todo : Add more conditions, such as making tiaras, suiciding, claiming talisman;
        isValidLap = true;
    }

    private boolean isOnStartTile() {
        return startLocation.hashCode() == Objects.requireNonNull(getPlayerLocation()).hashCode();
    }

    private boolean isOnStopTile() {
        return stopLocation.hashCode() == Objects.requireNonNull(getPlayerLocation()).hashCode();
    }

    private WorldPoint getPlayerLocation() {
        Player local = client.getLocalPlayer();
        if (local == null) {
            return null;
        }

        WorldPoint location = local.getWorldLocation();

        return location;
    }

    private void startLap() {
        // Reset lap attributes
        resetLap();

        // Start timer
        stopWatch.start();

        // Set lap started
        lapStarted = true;
    }

    private void stopLap() {
        // End timer
        stopWatch.pause();

        // Register lap
        stopWatch.lap();

        // Increment lap count
        session.incrementLapCount(client, xpTrackerService);

        // Clear lap data
        resetLap();
    }

    private void resetLap() {
        lapStarted = false;
        isValidLap = false;
    }
}
