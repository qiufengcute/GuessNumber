package net.mcreator.guessnumber.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.guessnumber.network.GuessnumberModVariables;
import net.mcreator.guessnumber.init.GuessnumberModMenus;

public class OKProcedure {
	public static boolean eventResult = true;

	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		{
			GuessnumberModVariables.PlayerVariables _vars = entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES);
			_vars.GuessCount = entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES).GuessCount + 1;
			_vars.markSyncDirty();
		}
		if (new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert((entity instanceof Player _entity0 && _entity0.containerMenu instanceof GuessnumberModMenus.MenuAccessor _menu0) ? _menu0.getMenuState(0, "YourNumber", "") : "") == entity
				.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES).Anwser) {
			{
				GuessnumberModVariables.PlayerVariables _vars = entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES);
				_vars.IsOK = true;
				_vars.markSyncDirty();
			}
			if (entity instanceof Player _player) {
				_player.containerMenu = _player.inventoryMenu;
			}
			if (world instanceof ServerLevel _level) {
				_level.getServer().getPlayerList()
						.broadcastSystemMessage(Component
								.literal(("[GuessNumber] " + entity.getDisplayName().getString() + Component.translatable("yes.3").getString()
										+ new java.text.DecimalFormat("##.##").format(entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES).GuessCount) + Component.translatable("yes.5").getString()))
								.withColor(0x00ffcc).withStyle(ChatFormatting.BOLD), false);
			}
		} else if (new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert((entity instanceof Player _entity6 && _entity6.containerMenu instanceof GuessnumberModMenus.MenuAccessor _menu6) ? _menu6.getMenuState(0, "YourNumber", "") : "") > entity
				.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES).Anwser) {
			{
				GuessnumberModVariables.PlayerVariables _vars = entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES);
				_vars.StatusIsBig = true;
				_vars.markSyncDirty();
			}
		} else {
			{
				GuessnumberModVariables.PlayerVariables _vars = entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES);
				_vars.StatusIsBig = false;
				_vars.markSyncDirty();
			}
		}
		{
			GuessnumberModVariables.PlayerVariables _vars = entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES);
			_vars.FirstGuess = false;
			_vars.markSyncDirty();
		}
	}
}