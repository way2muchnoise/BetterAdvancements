package betteradvancements.common.api.event;

import net.minecraft.advancements.Advancement;

public interface IAdvancementMovedEvent {
    Advancement getAdvancement();

    int getX();

    int getY();
}
