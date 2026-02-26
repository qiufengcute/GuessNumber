package net.mcreator.guessnumber;

import net.mcreator.guessnumber.network.GuessnumberModVariables;
import net.mcreator.guessnumber.init.GuessnumberModScreens;
import net.mcreator.guessnumber.init.GuessnumberModMenus;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ClientModInitializer;

@Environment(EnvType.CLIENT)
public class GuessnumberModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Start of user code block mod constructor
		// End of user code block mod constructor
		GuessnumberModScreens.clientLoad();
		GuessnumberModMenus.clientLoad();
		ClientPlayNetworking.registerGlobalReceiver(GuessnumberModVariables.SavedDataSyncMessage.TYPE, GuessnumberModVariables.SavedDataSyncMessage::handleData);
		// Start of user code block mod init
		// End of user code block mod init
	}
	// Start of user code block mod methods
	// End of user code block mod methods
}