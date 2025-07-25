package dev.koodaamo.foodium.entity.renderer;

import dev.koodaamo.foodium.FoodiumMod;
import dev.koodaamo.foodium.entity.SeaBat;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class SeaBatRenderer extends MobRenderer<SeaBat, SeaBatRenderState, SeaBatModel> {
	private static final ResourceLocation SEABAT_LOCATION = ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "textures/entity/seabat.png");

	public SeaBatRenderer(Context context) {
		super(context, new SeaBatModel(context.bakeLayer(ModelLayers.BAT)), 0.25F);
		this.addLayer(new SeaBatHeldItemLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(SeaBatRenderState p_365861_) {
		return SEABAT_LOCATION;
	}

	@Override
	public SeaBatRenderState createRenderState() {
		return new SeaBatRenderState();
	}

	public void extractRenderState(SeaBat seaBat, SeaBatRenderState renderState, float p_368671_) {
		super.extractRenderState(seaBat, renderState, p_368671_);
		renderState.flyAnimationState.copyFrom(seaBat.flyAnimationState);
		itemModelResolver.updateForLiving(renderState.heldItem, seaBat.getMainHandItem(), ItemDisplayContext.GROUND, seaBat);
		HoldingEntityRenderState.extractHoldingEntityRenderState(seaBat, renderState, itemModelResolver);
	}
}
