package net.mcreator.guessnumber.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.guessnumber.world.inventory.GuessNumberMenu;
import net.mcreator.guessnumber.network.GuessnumberModVariables;

import io.netty.buffer.Unpooled;

public class StartGuessRunProcedure {
	public static boolean eventResult = true;

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		{
			GuessnumberModVariables.PlayerVariables _vars = entity.getAttachedOrCreate(GuessnumberModVariables.PLAYER_VARIABLES);
			_vars.GuessCount = 0;
			_vars.Anwser = Mth.nextInt(RandomSource.create(), 1, 100);
			_vars.IsOK = false;
			_vars.FirstGuess = true;
			_vars.markSyncDirty();
		}
		if (entity instanceof ServerPlayer _ent) {
			BlockPos _bpos1 = BlockPos.containing(x, y, z);
			_ent.openMenu(new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("GuessNumber");
				}

				@Override
				public boolean shouldCloseCurrentScreen() {
					return false;
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new GuessNumberMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos1));
				}
			});
		}
	}
}