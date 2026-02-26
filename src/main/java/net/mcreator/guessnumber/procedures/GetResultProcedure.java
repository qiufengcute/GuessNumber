package net.mcreator.guessnumber.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

import net.mcreator.guessnumber.network.GuessnumberModVariables;

public class GetResultProcedure {
	public static boolean eventResult = true;

	public static String execute(LevelAccessor world) {
		if (!GuessnumberModVariables.MapVariables.get(world).FirstGuess) {
			if (GuessnumberModVariables.MapVariables.get(world).IsOK) {
				return Component.translatable("result.yes").getString();
			} else if (GuessnumberModVariables.MapVariables.get(world).StatusIsBig) {
				return Component.translatable("result.big").getString();
			} else {
				return Component.translatable("result.small").getString();
			}
		}
		return "";
	}
}