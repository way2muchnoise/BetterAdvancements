package betteradvancements.api.event;

import net.minecraft.advancements.Advancement;

import java.util.List;

public interface IAdvancementDrawConnectionsEvent {
    Advancement getAdvancement();
    List<Advancement> getExtraConnections();
}
