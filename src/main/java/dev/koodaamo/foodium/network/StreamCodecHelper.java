package dev.koodaamo.foodium.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.simple.SimpleFlow;

public interface StreamCodecHelper<T> extends StreamCodec<RegistryFriendlyByteBuf, T> {

	public static <T> StreamCodecHelper<T> create(BiConsumer<T, RegistryFriendlyByteBuf> encoder, Function<RegistryFriendlyByteBuf, T> decoder, BiConsumer<T, CustomPayloadEvent.Context> handler) {
		return new StreamCodecHelper<T>() {

			@Override
			public T decode(RegistryFriendlyByteBuf buffer) {
				return decoder.apply(buffer);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buffer, T packet) {
				encoder.accept(packet, buffer);
			}
		};
	}

	public static <T extends SimplePacket> StreamCodecHelper<T> create(Supplier<T> ctor) {
		return new StreamCodecHelper<T>() {

			@Override
			public T decode(RegistryFriendlyByteBuf buffer) {
				T t = ctor.get();
				t.decode(buffer);
				return t;
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buffer, T packet) {
				packet.encode(buffer);
			}
		};
	}

	public static <T extends SimplePacket> StreamCodecHelper<T> create(Function<RegistryFriendlyByteBuf, T> ctor) {
		return new StreamCodecHelper<T>() {

			@Override
			public T decode(RegistryFriendlyByteBuf buffer) {
				return ctor.apply(buffer);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buffer, T packet) {
				packet.encode(buffer);
			}
		};
	}

	public static <T extends SimplePacket> void handle(T packet, CustomPayloadEvent.Context context) {
		packet.handle(context);
	}

	public static <T extends SimplePacket> void register(Class<T> clazz, Function<RegistryFriendlyByteBuf, T> ctor, SimpleFlow<RegistryFriendlyByteBuf, Object> flow) {
		flow.add(clazz, StreamCodecHelper.create(ctor), StreamCodecHelper::handle);
	}

	/**
	 * @deprecated This method relies on reflection and is only for shortening your code. Unless you 100% know what you're doing, See the other implementations of register(...) instead for stability.
	 * @param clazz Some SimplePacket class that contains a constructor that takes a single RegistryFriendlyByteBuf argument.
	 * @param flow  An instance of SimpleFlow where the packet will be registered.
	 */
	public static <T extends SimplePacket> void register(Class<T> clazz, SimpleFlow<RegistryFriendlyByteBuf, Object> flow) {
		flow.add(clazz, StreamCodecHelper.create((buf) -> {
			try {
				return clazz.getConstructor(RegistryFriendlyByteBuf.class).newInstance(buf);
			} catch (Exception e) {
				try {
					T t =  clazz.getConstructor().newInstance();
					t.decode(buf);
					return t;
				} catch (Exception e2) {
					throw new IllegalStateException(clazz.getCanonicalName() + " does not have a default ctor or a ctor that takes a single RegistryFriendlyByteBuf argument. This is not a bug - Maybe read the docs next time ;)", e2);
				}
			}
		}), StreamCodecHelper::handle);
	}

	public interface SimplePacket {
		public abstract void encode(RegistryFriendlyByteBuf buffer);

		public abstract void decode(RegistryFriendlyByteBuf buffer);

		public abstract void handle(CustomPayloadEvent.Context context);
	}

}
