/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.guessnumber.init;

import net.mcreator.guessnumber.command.StartGuessCommand;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class GuessnumberModCommands {
	public static void load() {
		CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, environment) -> {
			StartGuessCommand.register(dispatcher, commandBuildContext, environment);
		});
	}
}