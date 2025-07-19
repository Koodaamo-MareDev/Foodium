package dev.koodaamo.foodium.registry;

import dev.koodaamo.foodium.FoodiumMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class FoodiumTags {
	
	public static final TagKey<Item> SEABAT_STEALABLE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "seabat_stealable"));

}
