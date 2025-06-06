package dev.koodaamo.foodium.block;

import dev.koodaamo.foodium.blockentity.MicrowaveBlockEntity;
import dev.koodaamo.foodium.gui.LazyDataSlot;
import dev.koodaamo.foodium.gui.MicrowaveMenu;
import dev.koodaamo.foodium.gui.SimpleDataSlot;
import dev.koodaamo.foodium.registry.FoodiumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MicrowaveBlock extends Block implements EntityBlock {

	public MicrowaveBlock(Properties props) {
		super(props.noOcclusion());
	}
	
	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider((containerId, playerInventory, player) -> {
			if(level.getBlockEntity(pos) instanceof MicrowaveBlockEntity microwaveBE) {
				ContainerLevelAccess access = ContainerLevelAccess.create(level, pos);
				LazyDataSlot cookTimeSlot = LazyDataSlot.shared(microwaveBE.cookTime);
				SimpleDataSlot processingSlot = SimpleDataSlot.shared(microwaveBE.processing);
				return new MicrowaveMenu(containerId, playerInventory, microwaveBE.getItemHandler(), access, cookTimeSlot, processingSlot, microwaveBE.totalCookTime);
			}
			return null;
		}, Component.literal("Microwave"));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(state.getMenuProvider(level, pos));
			return InteractionResult.CONSUME;
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MicrowaveBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return type == FoodiumBlockEntities.MICROWAVE_BLOCK_ENTITY_TYPE.get() ? MicrowaveBlockEntity::tick : null;
	}

	@Override
	public void destroy(LevelAccessor accessor, BlockPos pos, BlockState state) {
		if(accessor.getBlockEntity(pos) instanceof MicrowaveBlockEntity be) {
			dropResources(state, accessor, pos, be);
		}
	}
}
