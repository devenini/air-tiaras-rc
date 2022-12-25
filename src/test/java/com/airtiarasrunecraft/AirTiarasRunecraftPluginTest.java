package com.airtiarasrunecraft;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AirTiarasRunecraftPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AirTiarasRunecraftPlugin.class);
		RuneLite.main(args);
	}
}