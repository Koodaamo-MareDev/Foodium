package dev.koodaamo.foodium.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;

public class SeaBat extends Bat {

	
	// Constructor for entity
	public SeaBat(EntityType<? extends Bat> entityType, Level level_) {
		super(entityType, level_);
	}

	@Override
	public void tick() {
		super.tick();
		// Add your custom behavior here
	}
}