package betteradvancements.forge;

import betteradvancements.api.IBetterAdvancementEntryGui;
import betteradvancements.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.api.event.IAdvancementMovedEvent;
import betteradvancements.api.forge.event.AdvancementDrawConnectionsEvent;
import betteradvancements.api.forge.event.AdvancementMovedEvent;
import betteradvancements.platform.IEventHelper;
import net.minecraft.advancements.AdvancementNode;
import net.minecraftforge.common.MinecraftForge;

public class ForgeEventHelper implements IEventHelper {
    @Override
    public IAdvancementMovedEvent postAdvancementMovementEvent(IBetterAdvancementEntryGui gui) {
        final AdvancementMovedEvent event = new AdvancementMovedEvent(gui);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    @Override
    public IAdvancementDrawConnectionsEvent postAdvancementDrawConnectionsEvent(AdvancementNode advancement) {
        final AdvancementDrawConnectionsEvent event = new AdvancementDrawConnectionsEvent(advancement);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
}
