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
		GuessnumberModVariables.MapVariables.get(world).GuessCount = GuessnumberModVariables.MapVariables.get(world).GuessCount + 1;
		GuessnumberModVariables.MapVariables.get(world).markSyncDirty();
		if (new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert((entity instanceof Player _entity0 && _entity0.containerMenu instanceof GuessnumberModMenus.MenuAccessor _menu0) ? _menu0.getMenuState(0, "YourNumber", "") : "") == GuessnumberModVariables.MapVariables.get(world).Anwser) {
			GuessnumberModVariables.MapVariables.get(world).IsOK = true;
			GuessnumberModVariables.MapVariables.get(world).markSyncDirty();
			if (entity instanceof Player _player) {
				_player.containerMenu = _player.inventoryMenu;
			}
			if (world instanceof ServerLevel _level) {
				_level.getServer().getPlayerList()
						.broadcastSystemMessage(
								Component
										.literal(("[GuessNumber] " + entity.getDisplayName().getString() + Component.translatable("yes.3").getString()
												+ new java.text.DecimalFormat("##.##").format(GuessnumberModVariables.MapVariables.get(world).GuessCount) + Component.translatable("yes.5").getString()))
										.withColor(0x00ffcc).withStyle(ChatFormatting.BOLD),
								false);
			}
		} else if (new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert((entity instanceof Player _entity6 && _entity6.containerMenu instanceof GuessnumberModMenus.MenuAccessor _menu6) ? _menu6.getMenuState(0, "YourNumber", "") : "") > GuessnumberModVariables.MapVariables.get(world).Anwser) {
			GuessnumberModVariables.MapVariables.get(world).StatusIsBig = true;
			GuessnumberModVariables.MapVariables.get(world).markSyncDirty();
		} else {
			GuessnumberModVariables.MapVariables.get(world).StatusIsBig = false;
			GuessnumberModVariables.MapVariables.get(world).markSyncDirty();
		}
		GuessnumberModVariables.MapVariables.get(world).FirstGuess = false;
		GuessnumberModVariables.MapVariables.get(world).markSyncDirty();
	}
}