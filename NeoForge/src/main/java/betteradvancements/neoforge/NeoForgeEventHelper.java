package betteradvancements.neoforge;

import betteradvancements.common.api.IBetterAdvancementEntryGui;
import betteradvancements.common.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.common.api.event.IAdvancementMovedEvent;
import betteradvancements.neoforge.api.event.AdvancementDrawConnectionsEvent;
import betteradvancements.neoforge.api.event.AdvancementMovedEvent;
import betteradvancements.common.platform.IEventHelper;
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
