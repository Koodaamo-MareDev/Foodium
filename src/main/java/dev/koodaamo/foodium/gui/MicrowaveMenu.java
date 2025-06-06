package dev.koodaamo.foodium.gui;

import dev.koodaamo.foodium.blockentity.MicrowaveBlockEntity;
import dev.koodaamo.foodium.registry.FoodiumBlocks;
import dev.koodaamo.foodium.registry.FoodiumMenus;
import dev.koodaamo.sampoint.SampoInt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MicrowaveMenu extends AbstractContainerMenu {

	private int containerSlotCount;
	private ContainerLevelAccess access;
	private LazyDataSlot timeDataSlot;
	private DataSlot processingDataSlot;
	private SampoInt totalCookTime;

	public MicrowaveMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, new ItemStackHandler(1), ContainerLevelAccess.NULL, LazyDataSlot.standalone(), DataSlot.standalone(), new SampoInt()); // Use access to get the block entity
	}

	public MicrowaveMenu(int containerId, Inventory inventory, IItemHandler handler, ContainerLevelAccess access, LazyDataSlot timeDataSlot, DataSlot processingDataSlot, SampoInt totalCookTime) {
		super(FoodiumMenus.MICROWAVE.get(), containerId);
		this.addSlot(new SlotItemHandler(handler, 0, 48, 35));
		this.containerSlotCount = this.slots.size();
		this.addStandardInventorySlots(inventory, 8, 84);
		this.addDataSlot(timeDataSlot);
		this.addDataSlot(processingDataSlot);
		this.timeDataSlot = timeDataSlot;
		this.processingDataSlot = processingDataSlot;
		this.totalCookTime = totalCookTime;
		this.access = access;
	}

	public int getTime() {
		return timeDataSlot.get();
	}

	public void setTime(int time) {
		timeDataSlot.set(time);
	}

	// This is for determining the outcome and has to be updated using the stop or +30s buttons.
	public void setTotalTime(int time) {
		totalCookTime.set(time);
	}
	
	public int getTotalTime() {
		return totalCookTime.get();
	}

	public void invalidateTime() {
		timeDataSlot.invalidate();
	}

	public void setProcessing(boolean processing) {
		processingDataSlot.set(processing ? 1 : 0);
	}

	public boolean isProcessing() {
		return processingDataSlot.get() != 0;
	}

	@Override
	public boolean clickMenuButton(Player p_38875_, int p_38876_) {
		// TODO Auto-generated method stub
		return super.clickMenuButton(p_38875_, p_38876_);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
		// The quick moved slot stack
		ItemStack quickMovedStack = ItemStack.EMPTY;
		// The quick moved slot
		Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);

		// If the slot is in the valid range and the slot is not empty
		if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
			// Get the raw stack to move
			ItemStack rawStack = quickMovedSlot.getItem();
			// Set the slot stack to a copy of the raw stack
			quickMovedStack = rawStack.copy();

			/*
			 * The following quick move logic can be simplified to if in data inventory, try to move to player inventory/hotbar and vice versa for containers that cannot transform data (e.g. chests).
			 */

			// If the quick move was performed on the data inventory result slot
			if (quickMovedSlotIndex == 0) {
				// Try to move the result slot into the player inventory/hotbar
				if (!this.moveItemStackTo(rawStack, containerSlotCount, this.slots.size(), true)) {
					// If cannot move, no longer quick move
					return ItemStack.EMPTY;
				}
			}
			// Else if the quick move was performed on the player inventory or hotbar slot
			else if (quickMovedSlotIndex >= containerSlotCount && quickMovedSlotIndex < this.slots.size()) {
				// Try to move the inventory/hotbar slot into the data inventory input slots
				if (!this.moveItemStackTo(rawStack, 0, containerSlotCount, false)) {
					// If cannot move and in player inventory slot, try to move to hotbar
					if (quickMovedSlotIndex < containerSlotCount + 27) {
						if (!this.moveItemStackTo(rawStack, containerSlotCount + 27, this.slots.size(), false)) {
							// If cannot move, no longer quick move
							return ItemStack.EMPTY;
						}
					}
					// Else try to move hotbar into player inventory slot
					else if (!this.moveItemStackTo(rawStack, containerSlotCount, containerSlotCount + 27, false)) {
						// If cannot move, no longer quick move
						return ItemStack.EMPTY;
					}
				}
			}
			// Else if the quick move was performed on the data inventory input slots, try
			// to move to player inventory/hotbar
			else if (!this.moveItemStackTo(rawStack, containerSlotCount, this.slots.size(), false)) {
				// If cannot move, no longer quick move
				return ItemStack.EMPTY;
			}

			if (rawStack.isEmpty()) {
				// If the raw stack has completely moved out of the slot, set the slot to the
				// empty stack
				quickMovedSlot.set(ItemStack.EMPTY);
			} else {
				// Otherwise, notify the slot that that the stack count has changed
				quickMovedSlot.setChanged();
			}

			/*
			 * The following if statement and Slot#onTake call can be removed if the menu does not represent a container that can transform stacks (e.g. chests).
			 */
			if (rawStack.getCount() == quickMovedStack.getCount()) {
				// If the raw stack was not able to be moved to another slot, no longer quick
				// move
				return ItemStack.EMPTY;
			}
			// Execute logic on what to do post move with the remaining stack
			quickMovedSlot.onTake(player, rawStack);
		}

		return quickMovedStack; // Return the slot stack
	}

	// Assume this menu is attached to RegistryObject<Block> MY_BLOCK
	@Override
	public boolean stillValid(Player player) {
		return AbstractContainerMenu.stillValid(this.access, player, FoodiumBlocks.MICROWAVE_BLOCK.get());
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.access.execute((level, pos) -> {
			if (level.getBlockEntity(pos) instanceof MicrowaveBlockEntity be) {
				be.setChanged();
			}
		});
	}
}
