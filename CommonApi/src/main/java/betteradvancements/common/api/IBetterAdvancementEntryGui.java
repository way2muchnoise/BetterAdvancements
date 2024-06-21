package betteradvancements.common.api;

import net.minecraft.advancements.Advancement;

public interface IBetterAdvancementEntryGui {
    Advancement getAdvancement();

    int getX();

    int getY();
}
