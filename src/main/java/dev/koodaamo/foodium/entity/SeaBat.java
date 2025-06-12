package dev.koodaamo.foodium.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;

public class SeaBat extends Bat {

	public SeaBat(EntityType<? extends Bat> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void tick() {
		super.tick();
	}
}