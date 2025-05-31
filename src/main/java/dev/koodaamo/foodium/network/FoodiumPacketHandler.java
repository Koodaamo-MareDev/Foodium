package dev.koodaamo.foodium.network;

import dev.koodaamo.foodium.FoodiumMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.simple.SimpleConnection;

public class FoodiumPacketHandler {
	public static final SimpleChannel INSTANCE = ChannelBuilder.named(
			ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "main"))
			.serverAcceptedVersions((status, version) -> true)
			.clientAcceptedVersions((status, version) -> true)
			.networkProtocolVersion(1)
			.simpleChannel();
	
	public static void register() {
		INSTANCE.messageBuilder(UpdateMicrowaveTimePacket.class, NetworkDirection.PLAY_TO_SERVER)
		.encoder(UpdateMicrowaveTimePacket::encode)
		.decoder(UpdateMicrowaveTimePacket::new)
		.consumerMainThread(UpdateMicrowaveTimePacket::handle)
		.add();
	}
	
	public static void clientToServer(Object packet) {
		INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
	}
	
	public static void serverToClient(Object packet) {
		// TODO: implement S2C packet handling
	}
}
