package dev.koodaamo.foodium.network;

import java.util.HashMap;
import java.util.function.Consumer;

import dev.koodaamo.foodium.gui.MicrowaveMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class UpdateMicrowaveStatePacket implements StreamCodecHelper.SimplePacket {

	private static HashMap<Integer, Consumer<CustomPayloadEvent.Context>> handlers = new HashMap<>();

	public static final int ADD_TIME = registerHandler(0, UpdateMicrowaveStatePacket::addTime);
	public static final int START_PROCESSING = registerHandler(1, UpdateMicrowaveStatePacket::startProcessing);
	public static final int STOP_PROCESSING = registerHandler(2, UpdateMicrowaveStatePacket::stopProcessing);
	public static final int TOGGLE_PROCESSING = registerHandler(3, UpdateMicrowaveStatePacket::toggleProcessing);

	public int state;

	public static int registerHandler(int number, Consumer<CustomPayloadEvent.Context> handler) {
		if (handlers.containsKey(number))
			throw new IllegalArgumentException("Handler with type " + number + " already exists");
		handlers.put(number, handler);
		return number;
	}

	public UpdateMicrowaveStatePacket(int state) {
		this.state = state;
	}

	public UpdateMicrowaveStatePacket(RegistryFriendlyByteBuf buf) {
		decode(buf);
	}

	@Override
	public void decode(RegistryFriendlyByteBuf buf) {
		state = buf.readInt();
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buf) {
		buf.writeInt(state);
	}

	@Override
	public void handle(CustomPayloadEvent.Context context) {
		// Skip handling on client - shouldn't happen for now but better safe than sorry
		if (context.isClientSide()) {
			return;
		}

		Consumer<CustomPayloadEvent.Context> handler = handlers.get(state);
		if (handler != null) {
			// Don't process on network thread - wrapping code in this ensures thread safety.
			context.enqueueWork(() -> {
				handler.accept(context);
			});
		}
	}

	public static void addTime(CustomPayloadEvent.Context context) {
		ServerPlayer player = context.getSender();

		if (player.containerMenu instanceof MicrowaveMenu menu) {
			menu.setTime(menu.getTime() + 600);
			menu.setTotalTime(menu.getTotalTime() + 600);
			menu.invalidateTime();
		}
	}

	public static void startProcessing(CustomPayloadEvent.Context context) {
		ServerPlayer player = context.getSender();

		if (player.containerMenu instanceof MicrowaveMenu menu) {
			menu.setProcessing(true);
		}
	}

	public static void stopProcessing(CustomPayloadEvent.Context context) {
		ServerPlayer player = context.getSender();

		if (player.containerMenu instanceof MicrowaveMenu menu) {
			menu.setProcessing(false);
			menu.setTotalTime(0);
			menu.setTime(0);
		}
	}

	public static void toggleProcessing(CustomPayloadEvent.Context context) {
		ServerPlayer player = context.getSender();

		if (player.containerMenu instanceof MicrowaveMenu menu) {
			menu.setProcessing(!menu.isProcessing());
			if (!menu.isProcessing()) {
				menu.setTotalTime(0);
				menu.setTime(0);
			}
		}
	}
}
