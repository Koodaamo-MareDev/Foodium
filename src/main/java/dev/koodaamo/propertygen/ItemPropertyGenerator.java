package dev.koodaamo.propertygen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemPropertyGenerator implements IPropertyGenerator {

	private final String modId;
	private final DeferredRegister<Item> itemRegister;
	private final HashMap<String, RegistryObject<Item>> registeredItems;

	public ItemPropertyGenerator(String modId) {
		this.modId = modId;
		this.itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
		this.registeredItems = new HashMap<>();
	}

	@Override
	public void register(IEventBus modEventBus) {
		itemRegister.register(modEventBus);
	}

	/**
	 * Registers all items from the default directory
	 */
	public ItemPropertyGenerator registerAll() {
		return registerAll("");
	}

	/**
	 * Registers all items from a directory
	 * 
	 * @param location Directory containing item description files
	 */
	@Override
	public ItemPropertyGenerator registerAll(String location) {
		IModFile modFile = ModList.get().getModFileById(modId).getFile();
		Path root = modFile.findResource("data/" + modId + "/items/" + location);

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
	 * Gets the RegistryObject for a registered item. Useful for adding custom
	 * behavior such as advanced rendering
	 * 
	 * @param name The item name
	 * @return The RegistryObject corresponding the name or null if not found
	 */
	public RegistryObject<Item> getItem(String name) {
		return registeredItems.get(name);
	}

	/**
	 * Registers a single item from a file
	 * 
	 * @deprecated You should use {@link ItemPropertyGenerator#registerAll(String)}
	 *             instead
	 * @param location Location of the item description file
	 */
	@Override
	@Deprecated
	public ItemPropertyGenerator registerOne(String location) {
		IModFile modFile = ModList.get().getModFileById(modId).getFile();
		Path path = modFile.findResource("data/" + modId + "/items/" + location);
		if (Files.isRegularFile(path) && path.toString().endsWith(".json")) {
			registerFrom(location, path);
		}
		return this;
	}

	/**
	 * @param name The name of item.
	 * @param path The location of the json object
	 */
	private void registerFrom(String name, Path path) {
		try (InputStream is = Files.newInputStream(path)) {
			JsonObject json = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
			final Item.Properties properties = fromJson(json);
			registeredItems.put(name, itemRegister.register(name, () -> new Item(properties.setId(itemRegister.key(name)))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Item.Properties fromJson(JsonObject root) {
		Item.Properties properties = new Item.Properties();
		JsonAccessor accessor = new JsonAccessor(root);
		accessor.withAccessor("food", (food) -> {
			FoodProperties.Builder foodProperties = new FoodProperties.Builder();
			food.withInt("nutrition", foodProperties::nutrition);
			food.withFloat("saturation_modifier", foodProperties::saturationModifier);
			food.withBoolean("always_eat", foodProperties::alwaysEdible);
			properties.food(foodProperties.build());
		});
		accessor.withBoolean("fire_resistant", properties::fireResistant);
		accessor.withInt("max_stack", properties::stacksTo);
		accessor.withInt("durability", properties::durability);
		accessor.withString("rarity", (rarity) -> {
			properties.rarity(Rarity.valueOf(rarity));
		});
		accessor.withFloat("use_cooldown", properties::useCooldown);
		accessor.withAccessor("tool", (tool) -> {
			tool.withInt("enchantable", properties::enchantable);
			ToolDescriptor descriptor = new ToolDescriptor();
			tool.withFloat("attack_damage", descriptor::setAttackDamage);
			tool.withFloat("attack_speed", descriptor::setAttackSpeed);
			tool.withString("material", descriptor::setMaterial);
			tool.withString("type", descriptor::setMines);
			properties.tool(descriptor.getMaterial(), descriptor.getMines(), descriptor.attackDamage,
					descriptor.attackSpeed, descriptor.isAxe ? 5.0f : 0.0f);
		});
		return properties;
	}

	public class ToolDescriptor {
		private static HashMap<String, ToolMaterial> toolMaterials = new HashMap<>();
		private static HashMap<String, TagKey<Block>> toolTypes = new HashMap<>();

		static {
			toolMaterials.put("wood", ToolMaterial.WOOD);
			toolMaterials.put("stone", ToolMaterial.STONE);
			toolMaterials.put("iron", ToolMaterial.IRON);
			toolMaterials.put("diamond", ToolMaterial.DIAMOND);
			toolMaterials.put("gold", ToolMaterial.GOLD);
			toolMaterials.put("netherite", ToolMaterial.NETHERITE);
			toolTypes.put("pickaxe", BlockTags.MINEABLE_WITH_PICKAXE);
			toolTypes.put("axe", BlockTags.MINEABLE_WITH_AXE);
			toolTypes.put("hoe", BlockTags.MINEABLE_WITH_HOE);
			toolTypes.put("shovel", BlockTags.MINEABLE_WITH_SHOVEL);
			toolTypes.put("sword", BlockTags.SWORD_EFFICIENT);
		}

		public ToolMaterial material = ToolMaterial.WOOD;
		public TagKey<Block> mines = BlockTags.MINEABLE_WITH_PICKAXE;
		public float attackDamage = 0f;
		public float attackSpeed = 0f;
		public boolean isAxe = false;

		public ToolMaterial getMaterial() {
			return material;
		}

		public void setMaterial(String material) {
			if (material.equals("axe"))
				isAxe = true;
			this.material = toolMaterials.getOrDefault(material, this.material);
		}

		public TagKey<Block> getMines() {
			return mines;
		}

		public void setMines(String toolType) {
			this.mines = toolTypes.getOrDefault(toolType, this.mines);
		}

		public void setAttackDamage(float attackDamage) {
			this.attackDamage = attackDamage;
		}

		public void setAttackSpeed(float attackSpeed) {
			this.attackSpeed = attackSpeed;
		}

	}

}
