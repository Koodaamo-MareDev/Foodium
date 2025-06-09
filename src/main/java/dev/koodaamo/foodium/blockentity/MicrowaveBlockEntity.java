package dev.koodaamo.foodium.blockentity;

import dev.koodaamo.foodium.registry.FoodiumBlockEntities;
import dev.koodaamo.foodium.registry.FoodiumItems;
import dev.koodaamo.sampoint.AnypoInt;
import dev.koodaamo.sampoint.SampoInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem;
import net.minecraftforge.items.ItemStackHandler;

public class MicrowaveBlockEntity extends BlockEntity implements BlockContainerSingleItem {
	private final ItemStackHandler itemHandler = new ItemStackHandler(1);

	public SampoInt totalCookTime = new SampoInt();

	public AnypoInt cookTime = new AnypoInt(this::setChanged);
	public AnypoInt processing = new AnypoInt(this::setChanged);

	public MicrowaveBlockEntity(BlockPos pos, BlockState state) {
		super(FoodiumBlockEntities.MICROWAVE_BLOCK_ENTITY_TYPE.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (blockEntity instanceof MicrowaveBlockEntity microwave) {
			microwave.tick(level, pos, state);
		}
	}

	public void tick(Level level, BlockPos pos, BlockState state) {

		// Tick processing
		if (processing.get() != 0) {
			int oldTime = cookTime.get();
			if (oldTime > 0) {
				// Decrement timer
				cookTime.set(oldTime - 1);
			}

			// The result is handled on the server
			if (level.isClientSide())
				return;

			int newTime = cookTime.get();
			if (newTime == 0) {

				// Check if it finished just now
				if (newTime != oldTime) {
					// Finish cooking
					finishCooking(totalCookTime.get());
					totalCookTime.set(0);
				}

				/*
				 * Stop processing - Should happen only in 2 cases:
				 * - when cooking is finished.
				 * - when the start button is clicked but the timer is at 0
				 */
				processing.set(0);
			}
		}
	}

	public void finishCooking(int totalTakenTime) {
		if (totalTakenTime == 0)
			return;

		ItemStack item = getTheItem();
		int count = item.getCount();

		if (item.is(FoodiumItems.EXAMPLE_ITEM.get())) {
			if (totalTakenTime > 12000) {
				// The foodium burnt.
				setTheItem(new ItemStack(Items.CHARCOAL, count));
			} else {
				// Placeholder result
				setTheItem(new ItemStack(Items.GOLDEN_APPLE, count));
			}
		} else {
			setTheItem(new ItemStack(Items.CHARCOAL, count));
		}
	}

	@Override
	public void loadAdditional(CompoundTag tag, Provider provider) {
		super.loadAdditional(tag, provider);
		itemHandler.deserializeNBT(provider, tag.getCompoundOrEmpty("Inventory"));
		cookTime.set(tag.getIntOr("CookTime", 0));
		processing.set(tag.getBooleanOr("Processing", false) ? 1 : 0);
	}

	@Override
	public void saveAdditional(CompoundTag tag, Provider provider) {
		super.saveAdditional(tag, provider);
		tag.put("Inventory", itemHandler.serializeNBT(provider));
		tag.putInt("CookTime", cookTime.get());
		tag.putBoolean("Processing", processing.get() != 0);
	}

	public ItemStackHandler getItemHandler() {
		return itemHandler;
	}

	@Override
	public ItemStack getTheItem() {
		return itemHandler.getStackInSlot(0);
	}

	@Override
	public void setTheItem(ItemStack stack) {
		itemHandler.setStackInSlot(0, stack);
	}

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}
}