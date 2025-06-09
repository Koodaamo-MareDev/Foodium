package dev.koodaamo.foodium.registry;

import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class FoodiumEntityRenderers {
	
	public static void register() {
		EntityRenderers.register(FoodiumEntities.CUSTOM_BAT.get(), (context) -> new BatRenderer(context));
	}
}
