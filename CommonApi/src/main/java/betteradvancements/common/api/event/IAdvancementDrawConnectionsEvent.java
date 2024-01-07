package betteradvancements.common.api.event;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;

import java.util.List;

public interface IAdvancementDrawConnectionsEvent {
    AdvancementNode getAdvancement();
    List<AdvancementHolder> getExtraConnections();
}
