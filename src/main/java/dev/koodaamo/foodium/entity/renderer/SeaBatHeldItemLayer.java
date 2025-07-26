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
			pose.translate(getParentModel().body.x / 16, getParentModel().body.y / 16, getParentModel().body.z / 16);
			pose.scale(0.75f, 0.75f, 0.75f);
			pose.mulPose(Axis.YP.rotationDegrees(yaw));
			pose.mulPose(Axis.XP.rotationDegrees(getParentModel().body.xRot));
			pose.translate(0, 0.375f, .3125f);
			itemstackrenderstate.render(pose, source, light, OverlayTexture.NO_OVERLAY);
			pose.popPose();
		}
	}

}
