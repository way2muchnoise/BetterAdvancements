package betteradvancements.neoforge;

import betteradvancements.api.IBetterAdvancementEntryGui;
import betteradvancements.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.api.event.IAdvancementMovedEvent;
import betteradvancements.api.neoforge.event.AdvancementDrawConnectionsEvent;
import betteradvancements.api.neoforge.event.AdvancementMovedEvent;
import betteradvancements.platform.IEventHelper;
import net.minecraft.advancements.AdvancementNode;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeEventHelper implements IEventHelper {
    @Override
    public IAdvancementMovedEvent postAdvancementMovementEvent(IBetterAdvancementEntryGui gui) {
        final AdvancementMovedEvent event = new AdvancementMovedEvent(gui);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    @Override
    public IAdvancementDrawConnectionsEvent postAdvancementDrawConnectionsEvent(AdvancementNode advancement) {
        final AdvancementDrawConnectionsEvent event = new AdvancementDrawConnectionsEvent(advancement);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }
}
