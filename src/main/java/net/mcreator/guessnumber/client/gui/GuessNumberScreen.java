package net.mcreator.guessnumber.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import net.mcreator.guessnumber.world.inventory.GuessNumberMenu;
import net.mcreator.guessnumber.procedures.GetResultProcedure;
import net.mcreator.guessnumber.network.GuessNumberButtonMessage;
import net.mcreator.guessnumber.init.GuessnumberModScreens;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class GuessNumberScreen extends AbstractContainerScreen<GuessNumberMenu> implements GuessnumberModScreens.FabricScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private EditBox YourNumber;
	private Button button_ok;

	public GuessNumberScreen(GuessNumberMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 114;
	}

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		if (elementType == 0 && elementState instanceof String stringState) {
			if (name.equals("YourNumber"))
				YourNumber.setValue(stringState);
		}
		menuStateUpdateActive = false;
	}

	private static final ResourceLocation texture = ResourceLocation.parse("guessnumber:textures/screens/guess_number.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		YourNumber.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (YourNumber.isFocused())
			return YourNumber.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String YourNumberValue = YourNumber.getValue();
		super.resize(minecraft, width, height);
		YourNumber.setValue(YourNumberValue);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.guessnumber.guess_number.label_cai_shu_zi"), 57, 13, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.guessnumber.guess_number.label_number"), 68, 30, -12829636, false);
		guiGraphics.drawString(this.font, GetResultProcedure.execute(world), 7, 94, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		YourNumber = new EditBox(this.font, this.leftPos + 27, this.topPos + 42, 118, 18, Component.translatable("gui.guessnumber.guess_number.YourNumber"));
		YourNumber.setMaxLength(8192);
		YourNumber.setResponder(content -> {
			if (!menuStateUpdateActive)
				menu.sendMenuStateUpdate(entity, 0, "YourNumber", content, false);
		});
		YourNumber.setHint(Component.translatable("gui.guessnumber.guess_number.YourNumber"));
		this.addWidget(this.YourNumber);
		button_ok = Button.builder(Component.translatable("gui.guessnumber.guess_number.button_ok"), e -> {
			int x = GuessNumberScreen.this.x;
			int y = GuessNumberScreen.this.y;
			if (true) {
				ClientPlayNetworking.send(new GuessNumberButtonMessage(0, x, y, z));
				GuessNumberButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 65, this.topPos + 68, 35, 20).build();
		this.addRenderableWidget(button_ok);
	}
}