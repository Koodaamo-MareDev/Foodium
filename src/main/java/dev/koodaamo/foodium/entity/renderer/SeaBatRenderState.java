package dev.koodaamo.foodium.entity.renderer;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SeaBatRenderState extends LivingEntityRenderState {

	@OnlyIn(Dist.CLIENT)
	public boolean isResting;
	public final AnimationState flyAnimationState = new AnimationState();
	public final AnimationState restAnimationState = new AnimationState();
}
