package betteradvancements.neoforge.api.event;

import betteradvancements.common.api.IBetterAdvancementEntryGui;
import betteradvancements.common.api.event.IAdvancementMovedEvent;
import net.minecraft.advancements.Advancement;
import net.minecraftforge.eventbus.api.Event;

/**
 *  Event fired after an advancement has been moved in the gui and the mouse button has been released.
 */
public class AdvancementMovedEvent extends Event implements IAdvancementMovedEvent {
    /**
     * Advancement that has been moved.
     */
    private final Advancement advancement;
    /*
     * Coordinates the advancement was moved to.
     */
    private final int x, y;
    
    public AdvancementMovedEvent(IBetterAdvancementEntryGui gui) {
        this.advancement = gui.getAdvancement();
        this.x = gui.getX();
        this.y = gui.getY();
    }
    
    public Advancement getAdvancement() {
        return this.advancement;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
}
