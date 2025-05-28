package dev.koodaamo.foodium.registry;

import static dev.koodaamo.foodium.FoodiumMod.MODID;

import dev.koodaamo.foodium.gui.MicrowaveMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FoodiumMenus {

	public static DeferredRegister<MenuType<? extends AbstractContainerMenu>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

	public static final RegistryObject<MenuType<MicrowaveMenu>> MICROWAVE = MENUS.register("microwave", () -> new MenuType<MicrowaveMenu>(MicrowaveMenu::new, FeatureFlags.DEFAULT_FLAGS));

	public static void register(IEventBus eventBus) {
		MENUS.register(eventBus);
	}

}
