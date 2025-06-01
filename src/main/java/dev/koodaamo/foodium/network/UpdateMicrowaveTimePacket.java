package dev.koodaamo.foodium.network;

import dev.koodaamo.foodium.gui.MicrowaveMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class UpdateMicrowaveTimePacket implements StreamCodecHelper.SimplePacket {

	private BlockPos pos;
	private int time;

	public UpdateMicrowaveTimePacket(BlockPos pos, int time) {
		this.pos = pos;
		this.time = time;
	}
	
	public UpdateMicrowaveTimePacket(RegistryFriendlyByteBuf buf) {
		decode(buf);
	}

	@Override
	public void decode(RegistryFriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.time = buf.readInt();
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(time);
	}

	@Override
	public void handle(CustomPayloadEvent.Context context) {
		// Skip handling on client - shouldn't happen for now but better safe than sorry
		if (context.isClientSide()) {
			return;
		}
		
		// Don't process on network thread - wrapping code in this ensures thread safety.
		context.enqueueWork(() -> {
			// TODO: Handle packet
			ServerPlayer player = context.getSender();

			if (player.containerMenu instanceof MicrowaveMenu menu) {
				menu.setTime(time);
			}
		});
	}

}
