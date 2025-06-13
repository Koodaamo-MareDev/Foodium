package dev.koodaamo.foodium.entity.renderer;

import dev.koodaamo.foodium.FoodiumMod;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import net.minecraft.resources.ResourceLocation;

public class SeaBatRenderer extends BatRenderer {
	private static final ResourceLocation SEABAT_LOCATION = ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "textures/entity/seabat.png");

	public SeaBatRenderer(Context context) {
		super(context);
	}
	
	@Override
	public ResourceLocation getTextureLocation(BatRenderState p_365861_) {
        return SEABAT_LOCATION;
    }

}
