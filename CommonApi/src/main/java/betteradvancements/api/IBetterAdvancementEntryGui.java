package betteradvancements.api;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementNode;

public interface IBetterAdvancementEntryGui {
    AdvancementNode getAdvancement();

    int getX();

    int getY();
}
