package dev.koodaamo.foodium.registry;

import static dev.koodaamo.foodium.FoodiumMod.MODID;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FoodiumItems {

    // Create a Deferred Register to hold Items which will all be registered under the "foodium" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    // Creates a new BlockItem with the id "foodium:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block",
        () -> new BlockItem(FoodiumBlocks.EXAMPLE_BLOCK.get(), new Item.Properties().setId(ITEMS.key("example_block")))
    );
    
    // Creates a new food item with the id "foodium:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
        () -> new Item(new Item.Properties()
            .setId(ITEMS.key("example_item"))
            .food(new FoodProperties.Builder()
                .alwaysEdible()
                .nutrition(1)
                .saturationModifier(2f)
                .build()
            )
        )
    );
    
    public static void register(IEventBus eventBus) {
    	ITEMS.register(eventBus);
    }
}
