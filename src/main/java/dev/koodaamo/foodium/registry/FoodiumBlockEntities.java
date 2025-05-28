package dev.koodaamo.foodium.registry;

import static dev.koodaamo.foodium.FoodiumMod.MODID;

import java.util.Set;

import dev.koodaamo.foodium.blockentity.MicrowaveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FoodiumBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

	public static final RegistryObject<BlockEntityType<MicrowaveBlockEntity>> MICROWAVE_BLOCK_ENTITY_TYPE = BLOCKENTITIES.register("microwave_block_entity", () -> new BlockEntityType<MicrowaveBlockEntity>(MicrowaveBlockEntity::new, Set.of(FoodiumBlocks.MICROWAVE_BLOCK.get())));

	public static void register(IEventBus modEventBus) {
		BLOCKENTITIES.register(modEventBus);
	}
}