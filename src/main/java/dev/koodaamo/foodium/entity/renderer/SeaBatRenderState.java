package dev.koodaamo.foodium.entity.renderer;

import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SeaBatRenderState extends HoldingEntityRenderState {

	@OnlyIn(Dist.CLIENT)
	public boolean isResting;
	public final AnimationState flyAnimationState = new AnimationState();
	public final AnimationState restAnimationState = new AnimationState();
}
