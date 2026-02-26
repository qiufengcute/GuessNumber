package net.mcreator.guessnumber.network;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.guessnumber.procedures.OKProcedure;
import net.mcreator.guessnumber.GuessnumberMod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public record GuessNumberButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {

	public static final Type<GuessNumberButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GuessnumberMod.MODID, "guess_number_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, GuessNumberButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, GuessNumberButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new GuessNumberButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
	@Override
	public Type<GuessNumberButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final GuessNumberButtonMessage message, final ServerPlayNetworking.Context context) {
		context.server().execute(() -> handleButtonAction(context.player(), message.buttonID, message.x, message.y, message.z));
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

			OKProcedure.execute(world, entity);
		}
	}
}