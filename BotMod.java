package pl.example.botmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BotMod implements ModInitializer {
    public static final String MOD_ID = "botmod";

    public static final EntityType<BotEntity> BOT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "bot"),
            EntityType.Builder.create(BotEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 1.8f)
                    .build()
    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(BOT_ENTITY, BotEntity.createAttributes());
        BotNetworking.register();
    }
}
