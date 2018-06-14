package betteradvancements.api.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.advancements.Advancement;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 *  Event fired during advancement connection drawing.
 *  
 *  Used for adding extra connection lines.
 */
public class AdvancementDrawConnectionsEvent extends Event {
    /**
     * Advancement having its connection lines drawn.
     */
    private final Advancement advancement;
    /**
     * Extra connections to draw lines to.
     */
    private final List<Advancement> extraConnections;
    
    public AdvancementDrawConnectionsEvent(Advancement advancement) {
        this.advancement = advancement;
        this.extraConnections = new ArrayList<Advancement>();
    }
    
    public Advancement getAdvancement() {
        return this.advancement;
    }

    public List<Advancement> getExtraConnections() {
        return this.extraConnections;
    }
}
