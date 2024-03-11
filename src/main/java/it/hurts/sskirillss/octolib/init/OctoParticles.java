package it.hurts.sskirillss.octolib.init;

import it.hurts.sskirillss.octolib.OctoLib;
import it.hurts.sskirillss.octolib.particle.BasicColoredParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = OctoLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OctoParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, OctoLib.MODID);

    public static final RegistryObject<ParticleType<BasicColoredParticle.Options>> BASIC_COLORED = PARTICLES.register("basic_colored", BasicColoredParticle.Factory.Type::new);

    public static void register() {
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onParticleRegistry(RegisterParticleProvidersEvent event) {
        event.register(BASIC_COLORED.get(), BasicColoredParticle.Factory::new);
    }
}