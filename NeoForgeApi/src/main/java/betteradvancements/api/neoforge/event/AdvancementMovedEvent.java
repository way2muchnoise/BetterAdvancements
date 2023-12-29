package betteradvancements.api.neoforge.event;

import betteradvancements.api.IBetterAdvancementEntryGui;
import betteradvancements.api.event.IAdvancementMovedEvent;
import net.minecraft.advancements.AdvancementNode;
import net.neoforged.bus.api.Event;

/**
 *  Event fired after an advancement has been moved in the gui and the mouse button has been released.
 */
public class AdvancementMovedEvent extends Event implements IAdvancementMovedEvent {
    /**
     * Advancement that has been moved.
     */
    private final AdvancementNode advancement;
    /*
     * Coordinates the advancement was moved to.
     */
    private final int x, y;
    
    public AdvancementMovedEvent(IBetterAdvancementEntryGui gui) {
        this.advancement = gui.getAdvancement();
        this.x = gui.getX();
        this.y = gui.getY();
    }
    
    public AdvancementNode getAdvancement() {
        return this.advancement;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
}
