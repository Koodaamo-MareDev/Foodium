package dev.koodaamo.foodium.gui;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class FoodiumScreens {

	public static void register(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(FoodiumMenus.MICROWAVE.get(), MicrowaveScreen::new);
		});
	}
}
