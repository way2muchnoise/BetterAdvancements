package betteradvancements.common.api.event;

import net.minecraft.advancements.AdvancementNode;

public interface IAdvancementMovedEvent {
    AdvancementNode getAdvancement();

    int getX();

    int getY();
}
