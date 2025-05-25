package dev.koodaamo.propertygen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockPropertyGenerator implements IPropertyGenerator {

	private final String modId;
	private final DeferredRegister<Block> blockRegister;
	private final DeferredRegister<Item> itemRegister;
	private final HashMap<String, RegistryObject<Block>> registeredBlocks;

	public BlockPropertyGenerator(String modId) {
		this.modId = modId;
		this.blockRegister = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
		this.itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
		this.registeredBlocks = new HashMap<>();
	}
	
	@Override
	public void register(IEventBus modEventBus) {
		blockRegister.register(modEventBus);
		itemRegister.register(modEventBus);
	}

	/**
	 * Registers all blocks from the default directory
	 */
	public BlockPropertyGenerator registerAll() {
		return registerAll("");
	}

	/**
	 * Registers all blocks from a directory
	 * 
	 * @param location Directory containing block description files
	 */
	@Override
	public BlockPropertyGenerator registerAll(String location) {
		IModFile modFile = ModList.get().getModFileById(modId).getFile();
		Path root = modFile.findResource("data/" + modId + "/blocks/" + location);

		try (Stream<Path> paths = Files.walk(root)) {
			paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).forEach(path -> {
				String fileName = path.getFileName().toString();
				registerFrom(fileName.substring(0, fileName.lastIndexOf(".json")), path);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Gets the RegistryObject for a registered block. Useful for adding custom
	 * behavior such as advanced rendering
	 * 
	 * @param name The block name
	 * @return The RegistryObject corresponding the name or null if not found
	 */
	public RegistryObject<Block> getBlock(String name) {
		return registeredBlocks.get(name);
	}

	/**
	 * Registers a single block from a file
	 * 
	 * @deprecated You should use {@link BlockPropertyGenerator#registerAll(String)}
	 *             instead
	 * @param location Location of the block description file
	 */
	@Override
	@Deprecated
	public BlockPropertyGenerator registerOne(String location) {
		IModFile modFile = ModList.get().getModFileById(modId).getFile();
		Path path = modFile.findResource("data/" + modId + "/blocks/" + location);
		if (Files.isRegularFile(path) && path.toString().endsWith(".json")) {
			registerFrom(location, path);
		}
		return this;
	}

	/**
	 * @param name The name of block.
	 * @param path The location of the json object
	 */
	private void registerFrom(String name, Path path) {
		try (InputStream is = Files.newInputStream(path)) {
			JsonObject json = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

			final BlockBehaviour.Properties properties = fromJson(json);

			// Register the block itself.
			RegistryObject<Block> registryObject = blockRegister.register(name,
					() -> new Block(properties.setId(blockRegister.key(name))));

			// Register block item separately.
			new JsonAccessor(json).withBoolean("generate_item", () -> {
				itemRegister.register(name,
						() -> new BlockItem(registryObject.get(), new Item.Properties().setId(itemRegister.key(name))));
			});

			registeredBlocks.put(name, registryObject);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BlockBehaviour.Properties fromJson(JsonObject root) {
		BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
		JsonAccessor accessor = new JsonAccessor(root);

		accessor.withFloat("strength", properties::strength);
		accessor.withBoolean("instant_break", properties::instabreak);
		accessor.withFloat("destroy_time", properties::destroyTime);
		accessor.withFloat("explosion_resistance", properties::explosionResistance);
		accessor.withBoolean("no_collision", properties::noCollission);
		accessor.withBoolean("random_ticks", properties::randomTicks);
		accessor.withInt("emission", (emission) -> {
			properties.lightLevel((blockState) -> emission);
		});
		accessor.withInt("map_color", (colorId) -> {
			properties.mapColor(MapColor.byId(colorId));
		});
		accessor.withFloat("friction", properties::friction);
		accessor.withFloat("speed_factor", properties::speedFactor);
		accessor.withFloat("jump_factor", properties::jumpFactor);
		accessor.withBoolean("dynamic_shape", properties::dynamicShape);
		accessor.withBoolean("no_occlusion", properties::noOcclusion);
		accessor.withBoolean("burn", properties::ignitedByLava);
		accessor.withBoolean("require_tool", properties::requiresCorrectToolForDrops);

		return properties;
	}
}
