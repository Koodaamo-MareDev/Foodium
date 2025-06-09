package dev.koodaamo.foodium.network;

import dev.koodaamo.foodium.FoodiumMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.simple.SimpleFlow;

public class FoodiumPacketHandler {
	public static final SimpleChannel INSTANCE = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(FoodiumMod.MODID, "main")).serverAcceptedVersions((status, version) -> true).clientAcceptedVersions((status, version) -> true).networkProtocolVersion(1).simpleChannel();

	public static void register() {
		SimpleFlow<RegistryFriendlyByteBuf, Object> bidirectional = INSTANCE.play().bidirectional();

		// This is the correct practice - use this unless you truly find a reason not to
		StreamCodecHelper.register(UpdateMicrowaveStatePacket.class, UpdateMicrowaveStatePacket::new, bidirectional);

		// Uncomment the following line only if you want to play with fire.
		// StreamCodecHelper.register(UpdateMicrowaveTimePacket.class, bidirectional);
	}

	public static void clientToServer(StreamCodecHelper.SimplePacket packet) {
		INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
	}

	public static void serverToClient(StreamCodecHelper.SimplePacket packet) {
		// TODO: implement S2C packet handling
	}
}
