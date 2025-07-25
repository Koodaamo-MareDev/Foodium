package dev.koodaamo.foodium.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class SeaBatHeldItemLayer extends RenderLayer<SeaBatRenderState, SeaBatModel> {

	public SeaBatHeldItemLayer(RenderLayerParent<SeaBatRenderState, SeaBatModel> parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource source, int light, SeaBatRenderState state, float pitch, float yaw) {
		ItemStackRenderState itemstackrenderstate = state.heldItem;
		if (!itemstackrenderstate.isEmpty()) {
			pose.pushPose();
			pose.translate(this.getParentModel().body.x / 16.0F, this.getParentModel().body.y / 16.0F, this.getParentModel().body.z / 16.0F);
			pose.mulPose(Axis.YP.rotationDegrees(yaw));
			pose.translate(0, -.3125, 0);
			itemstackrenderstate.render(pose, source, light, OverlayTexture.NO_OVERLAY);
			pose.popPose();
		}
	}

}
