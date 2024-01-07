package betteradvancements.common.platform;

import betteradvancements.common.api.IBetterAdvancementEntryGui;
import betteradvancements.common.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.common.api.event.IAdvancementMovedEvent;
import net.minecraft.advancements.AdvancementNode;

public interface IEventHelper {
    IAdvancementMovedEvent postAdvancementMovementEvent(IBetterAdvancementEntryGui gui);

    IAdvancementDrawConnectionsEvent postAdvancementDrawConnectionsEvent(AdvancementNode advancement);
}
