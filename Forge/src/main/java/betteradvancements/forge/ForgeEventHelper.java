package betteradvancements.forge;

import betteradvancements.common.api.IBetterAdvancementEntryGui;
import betteradvancements.common.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.common.api.event.IAdvancementMovedEvent;
import betteradvancements.forge.api.event.AdvancementDrawConnectionsEvent;
import betteradvancements.forge.api.event.AdvancementMovedEvent;
import betteradvancements.common.platform.IEventHelper;
import net.minecraft.advancements.AdvancementNode;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

public class ForgeEventHelper implements IEventHelper {
    @Override
    public IAdvancementMovedEvent postAdvancementMovementEvent(IBetterAdvancementEntryGui gui) {
        final AdvancementMovedEvent event = new AdvancementMovedEvent(gui);
        AdvancementMovedEvent.BUS.fire(event);
        return event;
    }

    @Override
    public IAdvancementDrawConnectionsEvent postAdvancementDrawConnectionsEvent(AdvancementNode advancement) {
        final AdvancementDrawConnectionsEvent event = new AdvancementDrawConnectionsEvent(advancement);
        AdvancementDrawConnectionsEvent.BUS.fire(event);
        return event;
    }
}