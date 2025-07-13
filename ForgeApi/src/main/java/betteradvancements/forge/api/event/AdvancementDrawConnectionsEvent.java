package betteradvancements.forge.api.event;

import java.util.ArrayList;
import java.util.List;

import betteradvancements.common.api.event.IAdvancementDrawConnectionsEvent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;

/**
 *  Event fired during advancement connection drawing.
 *  
 *  Used for adding extra connection lines.
 */
public class AdvancementDrawConnectionsEvent extends MutableEvent implements IAdvancementDrawConnectionsEvent {
    public static final EventBus<AdvancementDrawConnectionsEvent> BUS = EventBus.create(AdvancementDrawConnectionsEvent.class);
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
