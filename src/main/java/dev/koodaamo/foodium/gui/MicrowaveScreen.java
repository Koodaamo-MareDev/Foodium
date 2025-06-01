package dev.koodaamo.foodium.gui;

import dev.koodaamo.foodium.FoodiumMod;
import dev.koodaamo.foodium.network.FoodiumPacketHandler;
import dev.koodaamo.foodium.network.UpdateMicrowaveTimePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MicrowaveScreen extends AbstractContainerScreen<MicrowaveMenu> {
	private static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "textures/gui/container/microwaving.png");

	public MicrowaveScreen(MicrowaveMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void init() {
		super.init();
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.addRenderableWidget(Button.builder(Component.literal("+30s"), this::onStartPressed).bounds(x + 103, y + 37, 34, 15).build());
	}

	private void onStartPressed(Button b) {
		// Modify the cached time
		int time = (this.menu.getTime() + 30) % 3600;
		FoodiumPacketHandler.clientToServer(new UpdateMicrowaveTimePacket(BlockPos.ZERO, time));
		// Update the backend and inform the server
		this.menu.setTime(time);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	private String getTimeStr() {
		int time = this.menu.getTime();
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
