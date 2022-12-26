package com.airtiarasrunecraft;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.EnumSet;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.LineComponent;

@Slf4j
class AirTiarasRunecraftOverlay extends Overlay
{
    private final Client client;
    private final AirTiarasRunecraftPlugin plugin;
    private final AirTiarasRunecraftConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private AirTiarasRunecraftOverlay(AirTiarasRunecraftPlugin plugin, Client client, AirTiarasRunecraftConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        AirTiarasRunecraftSession session = plugin.getSession();
        Stopwatch stopwatch = plugin.getStopWatch();

        panelComponent.getChildren().clear();
        String overlayTitle = "Air Tiaras Runecraft:";

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(overlayTitle)
                .build());

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(overlayTitle) + 10,
                0));

        if (config.showCurrentWorld()) {
            EnumSet<WorldType> worldType = client.getWorldType();
            Color currentWorldColor;

            if (worldType.contains(WorldType.MEMBERS))
            {
                currentWorldColor = Color.RED;
            }
            else
            {
                currentWorldColor = Color.WHITE;
            }

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("World:")
                    .right(Integer.toString(client.getWorld()))
                    .rightColor(currentWorldColor)
                    .build());
        }

        if (config.showLapTime()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Lap time:")
                    .right(stopwatch.secondsToString((int) stopwatch.getDisplayTime()))
                    .build());
        }

        if (config.showBestLap()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Best lap:")
                    .right((!stopwatch.getLaps().isEmpty()) ? stopwatch.getFormatedBestLap() : "--")
                    .rightColor((!stopwatch.getLaps().isEmpty()) ? Color.GREEN : Color.WHITE)
                    .build());
        }

        if (config.lapsToLevel() && session.getLapsTillGoal() > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Laps until goal:")
                    .right(Integer.toString(session.getLapsTillGoal()))
                    .build());
        }

        if (config.showTotalLaps())
        {
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Total laps:")
                .right(Integer.toString(session.getTotalLaps()))
                .build());
        }

        return panelComponent.render(graphics);
    }
}