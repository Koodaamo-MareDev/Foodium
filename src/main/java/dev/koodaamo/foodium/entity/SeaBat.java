package dev.koodaamo.foodium.entity;

import dev.koodaamo.foodium.FoodiumMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;

public class SeaBat extends Bat {

	public static final SoundEvent SOUND_SEABAT_DEATH = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "entity.seabat.death"));
	public static final SoundEvent SOUND_SEABAT_HURT = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "entity.seabat.hurt"));
	public static final SoundEvent SOUND_SEABAT_AMBIENT = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "entity.seabat.ambient"));

	public SeaBat(EntityType<? extends Bat> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return this.isResting() && this.random.nextInt(4) != 0 ? null : SOUND_SEABAT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_27451_) {
		return SOUND_SEABAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SOUND_SEABAT_DEATH;
	}

	@Override
	public void tick() {
		super.tick();
	}
}