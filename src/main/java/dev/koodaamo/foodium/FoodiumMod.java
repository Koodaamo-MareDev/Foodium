package dev.koodaamo.foodium;

import dev.koodaamo.foodium.network.FoodiumPacketHandler;
import dev.koodaamo.foodium.registry.FoodiumBlockEntities;
import dev.koodaamo.foodium.registry.FoodiumBlocks;
import dev.koodaamo.foodium.registry.FoodiumEntities;
import dev.koodaamo.foodium.registry.FoodiumEntityRenderers;
import dev.koodaamo.foodium.registry.FoodiumItems;
import dev.koodaamo.foodium.registry.FoodiumMenus;
import dev.koodaamo.foodium.registry.FoodiumScreens;
import dev.koodaamo.propertygen.BlockPropertyGenerator;
import dev.koodaamo.propertygen.ItemPropertyGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FoodiumMod.MODID)
public class FoodiumMod {
	// Define mod id in a common place for everything to reference
	public static final String MODID = "foodium";

	// Create a Deferred Register to hold CreativeModeTabs which will all be
	// registered under the "foodium" namespace
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

	// Creates a creative tab with the id "foodium:example_tab" for the example
	// item, that is placed after the combat tab
	public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> FoodiumItems.EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
		output.accept(FoodiumItems.EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred
														// over the event
	}).build());

	public FoodiumMod(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();

		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);
		// Register a block property generator
		BlockPropertyGenerator blockGen = new BlockPropertyGenerator(MODID);
		blockGen.registerAll();
		blockGen.register(modEventBus);

		// Register the Deferred Register to the mod event bus so blocks get registered
		FoodiumBlocks.register(modEventBus);

		FoodiumBlockEntities.register(modEventBus);

		// Register an item property generator
		ItemPropertyGenerator itemGen = new ItemPropertyGenerator(MODID);
		itemGen.registerAll();
		itemGen.register(modEventBus);

		// Register the Deferred Register to the mod event bus so items get registered
		FoodiumItems.register(modEventBus);

		// Register the Deferred Register to the mod event bus so tabs get registered
		CREATIVE_MODE_TABS.register(modEventBus);

		FoodiumMenus.register(modEventBus);

		// Register mod entities
		FoodiumEntities.register(modEventBus);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		// Register the item to a creative tab
		modEventBus.addListener(this::addCreative);

	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Some common setup code
		event.enqueueWork(() -> {
			FoodiumPacketHandler.register();
		});
	}

	// Add the items to the building blocks tab
	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			event.accept(FoodiumItems.EXAMPLE_BLOCK_ITEM);
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			FoodiumScreens.register(event);
			FoodiumEntityRenderers.register();
		}
	}

}
