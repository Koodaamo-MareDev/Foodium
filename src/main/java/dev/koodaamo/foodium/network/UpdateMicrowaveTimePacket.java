package dev.koodaamo.foodium.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerPacketListener;

public class UpdateMicrowaveTimePacket implements Packet<ServerPacketListener>{

	@Override
	public PacketType<? extends Packet<ServerPacketListener>> type() {
		return null;
	}
	
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

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(time);
    }

	@Override
	public void handle(ServerPacketListener listener) {
        PacketUtils.ensureRunningOnSameThread(this, listener, Minecraft.getInstance());
	}

}
