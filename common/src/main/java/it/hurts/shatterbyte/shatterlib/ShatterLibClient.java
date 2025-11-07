package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.shatterbyte.shatterlib.client.animation.TweenSystem;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.widget.CheckboxWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.TextAreaWidget;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibClientCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.dev.MyClientConfig;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.module.particle.trail.EntityTrailRegistry;
import it.hurts.shatterbyte.shatterlib.module.particle.trail.TestArrowTrail;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;
import net.minecraft.world.entity.EntityType;

public final class ShatterLibClient {
    public static void init() {
        registerEvents();
        ClientCommandRegistrationEvent.EVENT.register(ShatterLibClientCommand::register);

        TweenSystem.init();
        //EntityTrailRegistry.registerProvider(EntityType.ARROW, TestArrowTrail::new);
        EntryWidgetRegistry.register(boolean.class, CheckboxWidget::new);
        EntryWidgetRegistry.register(Boolean.class, CheckboxWidget::new);
        EntryWidgetRegistry.register(String.class, TextAreaWidget::new);

        ConfigManager.register(new MyClientConfig());
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(ShatterRenderManager::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ShatterRenderManager::worldExit);
    }

    public static double getDeltaTime() {
        return DeltaTimeTracker.getDeltaSeconds();
    }
}
