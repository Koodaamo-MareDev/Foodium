package dev.koodaamo.foodium.registry;

import static dev.koodaamo.foodium.FoodiumMod.MODID;

import dev.koodaamo.foodium.entity.SeaBat;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class FoodiumEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

	public static final RegistryObject<EntityType<SeaBat>> CUSTOM_BAT = register("sea_bat", EntityType.Builder.<SeaBat>of(SeaBat::new, MobCategory.MONSTER) // or CREATURE / MONSTER depending on your mob
			.sized(1.0F, 1.0F) // width, height
			.updateInterval(1));

	// This registers the default attributes for the living entities
	public static void register(IEventBus modEventBus) {
		ENTITY_TYPES.register(modEventBus);
	}

	public static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), ResourceLocation.fromNamespaceAndPath(MODID, name));

		return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(key));
	}

	@SubscribeEvent
	public static void createEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(CUSTOM_BAT.get(), Mob.createLivingAttributes().add(Attributes.MAX_HEALTH, 4).add(Attributes.FOLLOW_RANGE, 35).add(Attributes.SCALE, 1).build());
	}

	@SubscribeEvent
	public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
		event.register(CUSTOM_BAT.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SeaBat::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
	}
	
	//  Make golems attack SeaBats
	public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof SnowGolem snowGolem) {
            snowGolem.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                snowGolem, SeaBat.class, true
            ));
        }
        
        if (event.getEntity() instanceof IronGolem ironGolem) {
        	ironGolem.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
        			ironGolem, SeaBat.class, true
            ));
        }
    }
}