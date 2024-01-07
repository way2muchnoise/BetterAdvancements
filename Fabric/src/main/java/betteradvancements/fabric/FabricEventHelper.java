package betteradvancements.fabric;

import betteradvancements.common.api.IBetterAdvancementEntryGui;
import betteradvancements.common.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.common.api.event.IAdvancementMovedEvent;
import betteradvancements.api.fabric.event.AdvancementDrawConnectionsEvent;
import betteradvancements.common.platform.IEventHelper;
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
