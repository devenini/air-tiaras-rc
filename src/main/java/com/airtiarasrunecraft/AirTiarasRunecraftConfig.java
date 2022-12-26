package com.airtiarasrunecraft;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("airtiarasrunecraft")
public interface AirTiarasRunecraftConfig extends Config
{
	@ConfigItem(
			keyName = "showCurrentWorld",
			name = "Show Current World",
			description = "Enable/disable the current world",
			position = 1
	)
	default boolean showCurrentWorld()
	{
		return true;
	}
	@ConfigItem(
			keyName = "showLapTime",
			name = "Show Lap Time",
			description = "Enable/disable the lap time",
			position = 1
	)
	default boolean showLapTime()
	{
		return true;
	}
	@ConfigItem(
			keyName = "showBestLap",
			name = "Show Best Lap Time",
			description = "Enable/disable the best lap time",
			position = 1
	)
	default boolean showBestLap()
	{
		return true;
	}
	@ConfigItem(
			keyName = "showTotalLaps",
			name = "Show Total Laps Count",
			description = "Enable/disable the lap counter",
			position = 1
	)
	default boolean showTotalLaps()
	{
		return true;
	}

	@ConfigItem(
			keyName = "lapsToLevel",
			name = "Show Laps Until Goal",
			description = "Show number of laps remaining until next goal is reached.",
			position = 1
	)
	default boolean lapsToLevel()
	{
		return true;
	}
}
