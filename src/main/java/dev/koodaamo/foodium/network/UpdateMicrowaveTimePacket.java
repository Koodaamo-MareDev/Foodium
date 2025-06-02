package dev.koodaamo.foodium.network;

import dev.koodaamo.foodium.gui.MicrowaveMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class UpdateMicrowaveTimePacket implements StreamCodecHelper.SimplePacket {

	public UpdateMicrowaveTimePacket() {
		// Empty
	}
	
	public UpdateMicrowaveTimePacket(RegistryFriendlyByteBuf buf) {
		decode(buf);
	}

	@Override
	public void decode(RegistryFriendlyByteBuf buf) {
		// Empty
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf) {
		// Empty
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
				menu.setTime(menu.getTime() + 600);
				menu.invalidateTime();
			}
		});
	}

}
