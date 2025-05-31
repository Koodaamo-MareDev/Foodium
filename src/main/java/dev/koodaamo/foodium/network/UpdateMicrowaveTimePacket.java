package dev.koodaamo.foodium.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class UpdateMicrowaveTimePacket {

	private final BlockPos pos;
	private final int time;

	public UpdateMicrowaveTimePacket(BlockPos pos, int time) {
		this.pos = pos;
		this.time = time;
	}

	public UpdateMicrowaveTimePacket(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.time = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(time);
	}

	public void handle(CustomPayloadEvent.Context context) {
		// Skip handling on client - shouldn't happen for now but better safe than sorry
		if (context.isClientSide()) {
			return;
		}

		// Don't process on network thread - wrapping code in this ensures thread safety.
		context.enqueueWork(() -> {
			// TODO: Handle packet
		});
	}

}
