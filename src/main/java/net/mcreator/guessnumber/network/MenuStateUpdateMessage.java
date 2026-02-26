package net.mcreator.guessnumber.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;

import net.mcreator.guessnumber.init.GuessnumberModScreens;
import net.mcreator.guessnumber.init.GuessnumberModMenus;
import net.mcreator.guessnumber.GuessnumberMod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public record MenuStateUpdateMessage(int elementType, String name, Object elementState) implements CustomPacketPayload {

	public static final Type<MenuStateUpdateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GuessnumberMod.MODID, "menustate_update"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MenuStateUpdateMessage> STREAM_CODEC = StreamCodec.of(MenuStateUpdateMessage::write, MenuStateUpdateMessage::read);
	public static void write(FriendlyByteBuf buffer, MenuStateUpdateMessage message) {
		buffer.writeInt(message.elementType);
		buffer.writeUtf(message.name);
		if (message.elementType == 0) {
			buffer.writeUtf((String) message.elementState);
		} else if (message.elementType == 1) {
			buffer.writeBoolean((boolean) message.elementState);
		} else if (message.elementType == 2 && message.elementState instanceof Number n) {
			buffer.writeDouble(n.doubleValue());
		}
	}

	public static MenuStateUpdateMessage read(FriendlyByteBuf buffer) {
		int elementType = buffer.readInt();
		String name = buffer.readUtf();
		Object elementState = null;
		if (elementType == 0) {
			elementState = buffer.readUtf();
		} else if (elementType == 1) {
			elementState = buffer.readBoolean();
		} else if (elementType == 2) {
			elementState = buffer.readDouble();
		}
		return new MenuStateUpdateMessage(elementType, name, elementState);
	}

	@Override
	public Type<MenuStateUpdateMessage> type() {
		return TYPE;
	}

	public static void handleMenuState(final MenuStateUpdateMessage message, final ServerPlayNetworking.Context context) {
		if (message.name.length() > 256 || message.elementState instanceof String string && string.length() > 8192)
			return;
		context.server().execute(() -> {
			if (context.player().containerMenu instanceof GuessnumberModMenus.MenuAccessor menu) {
				menu.getMenuState().put(message.elementType + ":" + message.name, message.elementState);
				if (Minecraft.getInstance().screen instanceof GuessnumberModScreens.FabricScreenAccessor accessor) {
					accessor.updateMenuState(message.elementType, message.name, message.elementState);
				}
			}
		});
	}

	public static void handleClientMenuState(final MenuStateUpdateMessage message, final ClientPlayNetworking.Context context) {
		if (message.name.length() > 256 || message.elementState instanceof String string && string.length() > 8192)
			return;
		context.client().execute(() -> {
			if (context.player().containerMenu instanceof GuessnumberModMenus.MenuAccessor menu) {
				menu.getMenuState().put(message.elementType + ":" + message.name, message.elementState);
				if (Minecraft.getInstance().screen instanceof GuessnumberModScreens.FabricScreenAccessor accessor) {
					accessor.updateMenuState(message.elementType, message.name, message.elementState);
				}
			}
		});
	}
}