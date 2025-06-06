package dev.koodaamo.foodium.gui;

import dev.koodaamo.foodium.FoodiumMod;
import dev.koodaamo.foodium.network.FoodiumPacketHandler;
import dev.koodaamo.foodium.network.UpdateMicrowaveStatePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MicrowaveScreen extends AbstractContainerScreen<MicrowaveMenu> {
	private static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "textures/gui/container/microwaving.png");
	
	public boolean cachedProcessing = false;
	
	private Button startButton;
	
	public MicrowaveScreen(MicrowaveMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void init() {
		super.init();
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		
		// Add the +30 seconds button
		this.addRenderableWidget(Button.builder(Component.literal("+30s"), this::onAddTimePressed).bounds(x + 103, y + 36, 34, 15).build());
		
		// Add start/stop button
		this.addRenderableWidget(startButton = Button.builder(Component.literal(menu.isProcessing() ? "Stop" : "Start"), this::onStartPressed).bounds(x + 103, y + 55, 34, 15).build());
	}

	private void onAddTimePressed(Button b) {
		// Update the menu (this will be overridden by the server but the display should update immediately)
		this.menu.setTime((this.menu.getTime() + 600) % 72000);
		
		// Send the update packet to the server
		FoodiumPacketHandler.clientToServer(new UpdateMicrowaveStatePacket(UpdateMicrowaveStatePacket.ADD_TIME));
	}
	
	private void onStartPressed(Button b) {
		// Toggle the processing state
		boolean newState = !this.menu.isProcessing();

		// Apply the state display
		this.menu.setProcessing(newState);
		
		// Send toggle packet to the server
		FoodiumPacketHandler.clientToServer(new UpdateMicrowaveStatePacket(UpdateMicrowaveStatePacket.TOGGLE_PROCESSING));
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		
		boolean newState = menu.isProcessing();
		
		// Check for status changes
		if(cachedProcessing != newState) {
			// Update the start/stop button message
			startButton.setMessage(Component.literal(newState ? "Stop" : "Start"));
		}
		
		cachedProcessing = newState;
		
		this.renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	private String getTimeStr() {
		int time = this.menu.getTime() / 20;
		String min = (time / 60 < 10 ? "0" : "") + (time / 60);
		String sec = (time % 60 < 10 ? "0" : "") + (time % 60);
		return min + ":" + sec;
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		super.renderLabels(graphics, mouseX, mouseY);
		
		// Draw time label
		graphics.drawString(this.font, getTimeStr(), 107, 20, 0x4CFF00);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		graphics.blit(RenderType::guiTextured, BACKGROUND_LOCATION, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
	}

}
