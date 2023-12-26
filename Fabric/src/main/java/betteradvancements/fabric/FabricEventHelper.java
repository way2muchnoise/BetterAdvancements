package betteradvancements.fabric;

import betteradvancements.api.IBetterAdvancementEntryGui;
import betteradvancements.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.api.event.IAdvancementMovedEvent;
import betteradvancements.api.fabric.event.AdvancementDrawConnectionsEvent;
import betteradvancements.platform.IEventHelper;
import net.minecraft.advancements.AdvancementNode;

public class FabricEventHelper implements IEventHelper {
    @Override
    public IAdvancementMovedEvent postAdvancementMovementEvent(IBetterAdvancementEntryGui gui) {
        return null;
    }

    @Override
    public IAdvancementDrawConnectionsEvent postAdvancementDrawConnectionsEvent(AdvancementNode advancement) {
        IAdvancementDrawConnectionsEvent event = new AdvancementDrawConnectionsEvent(advancement);
        // TODO send event to other mods
        return event;
    }
}
