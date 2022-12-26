package com.airtiarasrunecraft;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

import java.util.function.Predicate;

import static net.runelite.api.Skill.*;

@PluginDescriptor(
        name = "Air Tiaras Runecraft"
)
@PluginDependency(XpTrackerPlugin.class)
@Slf4j
public class AirTiarasRunecraftPlugin extends Plugin {
    private final WorldPoint startLocation = new WorldPoint(2933, 3288, 0);
    private final WorldPoint stopLocation = new WorldPoint(2933, 3289, 0);
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
    private AirTiarasRunecraftSession session;
    @Inject
    private XpTrackerService xpTrackerService;
    private boolean lapStarted = false;
    @Getter
    @Setter
    private Stopwatch stopWatch;
    private boolean hasGainedRcXp = false;
    private boolean hasGainedMiningXp = false;
    private boolean hasGainedSmithingXp = false;
    private boolean hasGainedCraftingXp = false;
    private WorldPoint lastLocation;
    private boolean isFirstGameTick = true;

    @Provides
    AirTiarasRunecraftConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AirTiarasRunecraftConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        resetLap();

        session = new AirTiarasRunecraftSession();
        stopWatch = new Stopwatch("Stopwatch");

        lastLocation = getPlayerLocation();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(airTiarasRunecraftOverlay);
        session = null;
        stopWatch = null;
    }

    /**
     * Event listening every game tick.
     * @param tick
     */
    @Subscribe
    public void onGameTick(GameTick tick) {
        if(!isFirstGameTick) {
            try {
                if (isOnStopTile() && lapStarted && isValidLap()) {
                    // Stop the lap
                    stopLap();
                } else if (isOnStartTile() && !lapStarted && lastLocation.hashCode() == stopLocation.hashCode()) {
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
        } else {
            // first tick; save player location
            lastLocation = getPlayerLocation();

            isFirstGameTick = false;
        }
    }

    /**
     * Event listening to stat change.
     * @param statChanged
     */
    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if(isFirstGameTick) {
            return;
        }

        if (statChanged.getSkill() == RUNECRAFT) {
            hasGainedRcXp = true;
        } else if(statChanged.getSkill() == MINING) {
            hasGainedMiningXp = true;
        } else if(statChanged.getSkill() == CRAFTING) {
            hasGainedCraftingXp = true;
        } else if(statChanged.getSkill() == SMITHING) {
            hasGainedSmithingXp = true;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.HOPPING)
        {
            isFirstGameTick = true;
        }
    }

    private boolean isOnStartTile() {
        return startLocation.hashCode() == getPlayerLocation().hashCode();
    }

    private boolean isOnStopTile() {
        return stopLocation.hashCode() == getPlayerLocation().hashCode();
    }

    private boolean isValidLap() {
        return hasGainedRcXp && hasGainedCraftingXp && hasGainedSmithingXp && hasGainedMiningXp;
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

        // Update laps until goal
        session.calculateLapsUntilGoal(client, xpTrackerService);

        // Add overlay if it doesn't already exist
        if(!overlayManager.anyMatch(overlay -> overlay.getName() == airTiarasRunecraftOverlay.getName())) {
            overlayManager.add(airTiarasRunecraftOverlay);
        }
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
        hasGainedRcXp = false;
        hasGainedCraftingXp = false;
        hasGainedSmithingXp = false;
        hasGainedMiningXp = false;
    }
}
