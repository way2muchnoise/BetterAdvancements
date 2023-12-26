package betteradvancements.platform;

import betteradvancements.api.IBetterAdvancementEntryGui;
import betteradvancements.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.api.event.IAdvancementMovedEvent;
import net.minecraft.advancements.AdvancementNode;

public interface IEventHelper {
    IAdvancementMovedEvent postAdvancementMovementEvent(IBetterAdvancementEntryGui gui);

    IAdvancementDrawConnectionsEvent postAdvancementDrawConnectionsEvent(AdvancementNode advancement);
}
