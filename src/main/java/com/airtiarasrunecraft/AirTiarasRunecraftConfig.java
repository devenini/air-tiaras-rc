package com.airtiarasrunecraft;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("airtiarasrunecraft")
public interface AirTiarasRunecraftConfig extends Config
{
	@ConfigItem(
			keyName = "showLapCount",
			name = "Show Lap Count",
			description = "Enable/disable the lap counter",
			position = 1
	)
	default boolean showLapCount()
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
			keyName = "lapsToLevel",
			name = "Show Laps Until Goal",
			description = "Show number of laps remaining until next goal is reached.",
			position = 3
	)
	default boolean lapsToLevel()
	{
		return true;
	}

	@ConfigItem(
			keyName = "lapsPerHour",
			name = "Show Laps Per Hour",
			description = "Shows how many laps you can expect to complete per hour.",
			position = 4
	)
	default boolean lapsPerHour()
	{
		return true;
	}

	@ConfigItem(
			keyName = "lapTimeout",
			name = "Hide Lap Count",
			description = "Time until the lap counter hides/resets",
			position = 2
	)
	@Units(Units.MINUTES)
	default int lapTimeout()
	{
		return 30;
	}
}
