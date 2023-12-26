package betteradvancements.api.fabric.event;

import betteradvancements.api.event.IAdvancementDrawConnectionsEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;

import java.util.ArrayList;
import java.util.List;

/**
 *  Event fired during advancement connection drawing.
 *
 *  Used for adding extra connection lines.
 */
public class AdvancementDrawConnectionsEvent implements IAdvancementDrawConnectionsEvent {
    /**
     * Advancement having its connection lines drawn.
     */
    private final AdvancementNode advancement;
    /**
     * Extra connections to draw lines to.
     */
    private final List<AdvancementHolder> extraConnections;

    public AdvancementDrawConnectionsEvent(AdvancementNode advancement) {
        this.advancement = advancement;
        this.extraConnections = new ArrayList<>();
    }

    public AdvancementNode getAdvancement() {
        return this.advancement;
    }

    public List<AdvancementHolder> getExtraConnections() {
        return this.extraConnections;
    }
}
