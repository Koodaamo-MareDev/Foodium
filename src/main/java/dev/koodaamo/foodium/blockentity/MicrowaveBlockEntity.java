package dev.koodaamo.foodium.blockentity;

import dev.koodaamo.foodium.registry.FoodiumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class MicrowaveBlockEntity extends BlockEntity {
	private final ItemStackHandler itemHandler = new ItemStackHandler(1);

	public MicrowaveBlockEntity(BlockPos pos, BlockState state) {
		super(FoodiumBlockEntities.MICROWAVE_BLOCK_ENTITY_TYPE.get(), pos, state);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, Provider provider) {
		super.loadAdditional(tag, provider);
		itemHandler.deserializeNBT(provider, tag.getCompound("Inventory").get());
	}
	
	@Override
	public void saveAdditional(CompoundTag tag, Provider provider) {
		super.saveAdditional(tag, provider);
		tag.put("Inventory", itemHandler.serializeNBT(provider));
	}

	public ItemStackHandler getItemHandler() {
		return itemHandler;
	}

}