package betteradvancements.forge;

import betteradvancements.common.api.IBetterAdvancementEntryGui;
import betteradvancements.common.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.common.api.event.IAdvancementMovedEvent;
import betteradvancements.forge.api.event.AdvancementDrawConnectionsEvent;
import betteradvancements.forge.api.event.AdvancementMovedEvent;
import betteradvancements.common.platform.IEventHelper;
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