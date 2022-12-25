package com.airtiarasrunecraft;

import lombok.extern.slf4j.Slf4j;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
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
    public Dimension render(Graphics2D graphics)
    {
        AirTiarasRunecraftSession session = plugin.getSession();

        panelComponent.getChildren().clear();
        String overlayTitle = "Current Session:";

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(overlayTitle)
                .build());

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(overlayTitle) + 30,
                0));

        // Add a line on the overlay for world number
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current World:")
                .right(Integer.toString(client.getWorld()))
                .build());

        if (!config.showLapCount() ||
                session == null ||
                session.getLastLapCompleted() == null)
        {
            return null;
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Total Laps:")
                .right(Integer.toString(session.getTotalLaps()))
                .build());

        if (config.lapsToLevel() && session.getLapsTillGoal() > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Laps until goal:")
                    .right(Integer.toString(session.getLapsTillGoal()))
                    .build());
        }

        if (config.lapsPerHour() && session.getLapsPerHour() > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Laps per hour:")
                    .right(Integer.toString(session.getLapsPerHour()))
                    .build());
        }

        return panelComponent.render(graphics);
    }
}