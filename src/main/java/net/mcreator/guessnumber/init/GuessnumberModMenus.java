/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.guessnumber.init;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.client.Minecraft;

import net.mcreator.guessnumber.world.inventory.GuessNumberMenu;
import net.mcreator.guessnumber.network.MenuStateUpdateMessage;
import net.mcreator.guessnumber.GuessnumberMod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Map;

public class GuessnumberModMenus {
	public static MenuType<GuessNumberMenu> GUESS_NUMBER;

	public static void load() {
		GUESS_NUMBER = register("guess_number", GuessNumberMenu::new);
		GuessNumberMenu.screenInit();
		PayloadTypeRegistry.playC2S().register(MenuStateUpdateMessage.TYPE, MenuStateUpdateMessage.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(MenuStateUpdateMessage.TYPE, MenuStateUpdateMessage::handleMenuState);
	}

	public static void clientLoad() {
		PayloadTypeRegistry.playS2C().register(MenuStateUpdateMessage.TYPE, MenuStateUpdateMessage.STREAM_CODEC);
		ClientPlayNetworking.registerGlobalReceiver(MenuStateUpdateMessage.TYPE, MenuStateUpdateMessage::handleClientMenuState);
	}

	public interface MenuAccessor {
		Map<String, Object> getMenuState();

		Map<Integer, Slot> getSlots();

		default void sendMenuStateUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
			getMenuState().put(elementType + ":" + name, elementState);
			if (player instanceof ServerPlayer serverPlayer) {
				ServerPlayNetworking.send(serverPlayer, new MenuStateUpdateMessage(elementType, name, elementState));
			} else if (player.level().isClientSide) {
				if (Minecraft.getInstance().screen instanceof GuessnumberModScreens.FabricScreenAccessor accessor && needClientUpdate)
					accessor.updateMenuState(elementType, name, elementState);
				ClientPlayNetworking.send(new MenuStateUpdateMessage(elementType, name, elementState));
			}
		}

		default <T> T getMenuState(int elementType, String name, T defaultValue) {
			try {
				return (T) getMenuState().getOrDefault(elementType + ":" + name, defaultValue);
			} catch (ClassCastException e) {
				return defaultValue;
			}
		}
	}

	private static <M extends AbstractContainerMenu> MenuType<M> register(String registryname, MenuType.MenuSupplier<M> element) {
		return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(GuessnumberMod.MODID, registryname), new MenuType<>(element, FeatureFlags.DEFAULT_FLAGS));
	}
}