package betteradvancements.api.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementNode;

public interface IAdvancementMovedEvent {
    AdvancementNode getAdvancement();

    int getX();

    int getY();
}
