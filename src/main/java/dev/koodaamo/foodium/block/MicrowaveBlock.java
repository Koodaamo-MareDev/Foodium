package dev.koodaamo.foodium.block;

import dev.koodaamo.foodium.gui.MicrowaveMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MicrowaveBlock extends Block {

	public MicrowaveBlock(Properties props) {
		super(props.noOcclusion());
	}
	
	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider((containerId, playerInventory, player) -> new MicrowaveMenu(containerId, playerInventory), Component.literal("Microwave"));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(state.getMenuProvider(level, pos));
			return InteractionResult.CONSUME;
		}
		return InteractionResult.SUCCESS;
	}
}
