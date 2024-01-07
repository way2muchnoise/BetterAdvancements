package betteradvancements.neoforge;

import betteradvancements.common.platform.IEventHelper;
import betteradvancements.common.platform.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
    private final NeoForgeEventHelper eventHelper = new NeoForgeEventHelper();
    private final NeoForgeAdvancementVisitor advancementVisitor = new NeoForgeAdvancementVisitor();

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public IEventHelper getEventHelper() {
        return eventHelper;
    }

    @Override
    public NeoForgeAdvancementVisitor getAdvancementVisitor() {
        return advancementVisitor;
    }
}
