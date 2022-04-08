package betteradvancements.platform;

import betteradvancements.api.IBetterAdvancementEntryGui;
import betteradvancements.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.api.event.IAdvancementMovedEvent;
import net.minecraft.advancements.Advancement;

public interface IEventHelper {
    IAdvancementMovedEvent postAdvancementMovementEvent(IBetterAdvancementEntryGui gui);

    IAdvancementDrawConnectionsEvent postAdvancementDrawConnectionsEvent(Advancement advancement);
}
