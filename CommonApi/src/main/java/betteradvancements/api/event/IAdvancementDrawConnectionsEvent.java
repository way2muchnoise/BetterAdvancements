package betteradvancements.api.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;

import java.util.List;

public interface IAdvancementDrawConnectionsEvent {
    AdvancementNode getAdvancement();
    List<AdvancementHolder> getExtraConnections();
}
