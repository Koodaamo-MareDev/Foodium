package dev.koodaamo.foodium.block;

import static dev.koodaamo.foodium.FoodiumMod.MODID;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FoodiumBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	
    // Creates a new Block with the id "foodium:microwave"
    public static final RegistryObject<Block> MICROWAVE_BLOCK = BLOCKS.register("microwave",
        () -> new MicrowaveBlock(BlockBehaviour.Properties.of()
            .setId(BLOCKS.key("microwave"))
            .mapColor(MapColor.STONE).strength(0.5f).dynamicShape()
        )
    );

    // Creates a new Block with the id "foodium:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .setId(BLOCKS.key("example_block"))
            .mapColor(MapColor.STONE).strength(0.5f)
        )
    );
    
    public static void register(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
	}
    
}