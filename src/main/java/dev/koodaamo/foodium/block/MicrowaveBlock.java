package dev.koodaamo.foodium.block;

import dev.koodaamo.foodium.blockentity.MicrowaveBlockEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MicrowaveBlock extends Block implements EntityBlock {

	public MicrowaveBlock(Properties props) {
		super(props.noOcclusion());
	}
	
	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		MicrowaveBlockEntity microwaveBE = level.getBlockEntity(pos, FoodiumBlockEntities.MICROWAVE_BLOCK_ENTITY_TYPE.get()).get();
		return new SimpleMenuProvider((containerId, playerInventory, player) -> new MicrowaveMenu(containerId, playerInventory, microwaveBE.getItemHandler(), ContainerLevelAccess.create(level, pos), new SimpleDataSlot(microwaveBE::setCookTime, microwaveBE::getCookTime)), Component.literal("Microwave"));
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
	public void destroy(LevelAccessor accessor, BlockPos pos, BlockState state) {
		if(accessor.getBlockEntity(pos) instanceof MicrowaveBlockEntity be) {
			dropResources(state, accessor, pos, be);
		}
	}
}
