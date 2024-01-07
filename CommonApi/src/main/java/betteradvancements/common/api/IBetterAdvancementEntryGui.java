package betteradvancements.common.api;

import net.minecraft.advancements.AdvancementNode;

public interface IBetterAdvancementEntryGui {
    AdvancementNode getAdvancement();

    int getX();

    int getY();
}
