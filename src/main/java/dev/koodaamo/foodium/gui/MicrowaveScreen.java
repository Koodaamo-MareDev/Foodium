package dev.koodaamo.foodium.gui;

import dev.koodaamo.foodium.FoodiumMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MicrowaveScreen extends AbstractContainerScreen<MicrowaveMenu> {
	private static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "textures/gui/container/microwaving.png");

	private int time = 0;

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
		time += 30;
		time %= 3600;
		this.menu.setData(0, time);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	private String getTimeStr() {
		String min = (time / 60 < 10 ? "0" : "") + (time / 60);
		String sec = (time % 60 < 10 ? "0" : "") + (time % 60);
		return min + ":" + sec;
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		super.renderLabels(graphics, mouseX, mouseY);
		graphics.drawString(this.font, getTimeStr(), 107, 20, 0x4CFF00);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		graphics.blit(RenderType::guiTextured, BACKGROUND_LOCATION, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
	}

}
