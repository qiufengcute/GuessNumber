package net.mcreator.guessnumber.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.mcreator.guessnumber.network.GuessnumberModVariables;

public class GetResultProcedure {
	public static boolean eventResult = true;

	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		if (!entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES).FirstGuess) {
			if (entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES).IsOK) {
				return Component.translatable("result.yes").getString();
			} else if (entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES).StatusIsBig) {
				return Component.translatable("result.big").getString();
			} else {
				return Component.translatable("result.small").getString();
			}
		}
		return "";
	}
}