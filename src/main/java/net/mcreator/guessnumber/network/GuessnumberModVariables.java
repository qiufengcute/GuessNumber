package net.mcreator.guessnumber.network;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.mcreator.guessnumber.event.PlayerEvents;
import net.mcreator.guessnumber.GuessnumberMod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;

public class GuessnumberModVariables {
	public static final AttachmentType<PlayerVariables> PLAYER_VARIABLES = AttachmentRegistry.create(ResourceLocation.fromNamespaceAndPath(GuessnumberMod.MODID, "player_variables"),
			(builder) -> builder.persistent(PlayerVariables.CODEC).initializer(PlayerVariables::new));

	public static void variablesLoad() {
		PayloadTypeRegistry.playS2C().register(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC);
		ServerPlayerEvents.JOIN.register((player) -> {
			ServerPlayNetworking.send(player, new PlayerVariablesSyncMessage(player.getAttachedOrCreate(PLAYER_VARIABLES)));
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			ServerPlayNetworking.send(newPlayer, new PlayerVariablesSyncMessage(oldPlayer.getAttachedOrCreate(PLAYER_VARIABLES)));
		});
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			if (!destination.isClientSide())
				ServerPlayNetworking.send(player, new PlayerVariablesSyncMessage(player.getAttachedOrCreate(PLAYER_VARIABLES)));
		});
		PlayerEvents.END_PLAYER_TICK.register((entity) -> {
			if (entity instanceof ServerPlayer player && player.getAttachedOrCreate(PLAYER_VARIABLES)._syncDirty) {
				ServerPlayNetworking.send(player, new PlayerVariablesSyncMessage(player.getAttachedOrCreate(PLAYER_VARIABLES)));
				player.getAttachedOrCreate(PLAYER_VARIABLES)._syncDirty = false;
			}
		});
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			PlayerVariables original = oldPlayer.getAttachedOrCreate(PLAYER_VARIABLES);
			PlayerVariables clone = new PlayerVariables();
			clone.Anwser = original.Anwser;
			clone.IsOK = original.IsOK;
			clone.StatusIsBig = original.StatusIsBig;
			clone.FirstGuess = original.FirstGuess;
			clone.GuessCount = original.GuessCount;
			if (alive) {
			}
			newPlayer.setAttached(PLAYER_VARIABLES, clone);
		});
	}

	public static class PlayerVariables {
		public static final Codec<PlayerVariables> CODEC = RecordCodecBuilder.create(builder -> builder.group(Codec.DOUBLE.fieldOf("Anwser").orElse(0d).forGetter((vars) -> vars.Anwser),
				Codec.BOOL.fieldOf("IsOK").orElse(false).forGetter((vars) -> vars.IsOK), Codec.BOOL.fieldOf("StatusIsBig").orElse(false).forGetter((vars) -> vars.StatusIsBig),
				Codec.BOOL.fieldOf("FirstGuess").orElse(false).forGetter((vars) -> vars.FirstGuess), Codec.DOUBLE.fieldOf("GuessCount").orElse(0d).forGetter((vars) -> vars.GuessCount)).apply(builder, PlayerVariables::new));
		boolean _syncDirty = false;
		public double Anwser = 0;
		public boolean IsOK = false;
		public boolean StatusIsBig = false;
		public boolean FirstGuess = true;
		public double GuessCount = 0;

		public PlayerVariables() {
		}

		public PlayerVariables(double Anwser, boolean IsOK, boolean StatusIsBig, boolean FirstGuess, double GuessCount) {
			this.Anwser = Anwser;
			this.IsOK = IsOK;
			this.StatusIsBig = StatusIsBig;
			this.FirstGuess = FirstGuess;
			this.GuessCount = GuessCount;
		}

		public void serialize(ValueOutput output) {
			output.putDouble("Anwser", Anwser);
			output.putBoolean("IsOK", IsOK);
			output.putBoolean("StatusIsBig", StatusIsBig);
			output.putBoolean("FirstGuess", FirstGuess);
			output.putDouble("GuessCount", GuessCount);
		}

		public void deserialize(ValueInput input) {
			Anwser = input.getDoubleOr("Anwser", 0);
			IsOK = input.getBooleanOr("IsOK", false);
			StatusIsBig = input.getBooleanOr("StatusIsBig", false);
			FirstGuess = input.getBooleanOr("FirstGuess", false);
			GuessCount = input.getDoubleOr("GuessCount", 0);
		}

		public void markSyncDirty() {
			_syncDirty = true;
		}
	}

	public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GuessnumberMod.MODID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> {
			TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
			message.data.serialize(output);
			buffer.writeNbt(output.buildResult());
		}, (RegistryFriendlyByteBuf buffer) -> {
			PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables());
			message.data.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, buffer.registryAccess(), buffer.readNbt()));
			return message;
		});

		@Override
		public Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final PlayerVariablesSyncMessage message, final ClientPlayNetworking.Context context) {
			if (message.data != null) {
				context.client().execute(() -> {
					TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, context.player().registryAccess());
					message.data.serialize(output);
					context.player().getAttachedOrCreate(PLAYER_VARIABLES).deserialize(TagValueInput.create(ProblemReporter.DISCARDING, context.player().registryAccess(), output.buildResult()));
				});
			}
		}
	}
}