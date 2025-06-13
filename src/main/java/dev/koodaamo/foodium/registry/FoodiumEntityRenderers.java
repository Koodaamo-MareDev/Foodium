package dev.koodaamo.foodium.registry;

import dev.koodaamo.foodium.entity.renderer.SeaBatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class FoodiumEntityRenderers {
	
	public static void register() {
		EntityRenderers.register(FoodiumEntities.CUSTOM_BAT.get(), (context) -> new SeaBatRenderer(context));
	}
}
