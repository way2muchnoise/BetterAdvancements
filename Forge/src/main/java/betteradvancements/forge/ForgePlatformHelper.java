package betteradvancements.forge;

import betteradvancements.platform.IEventHelper;
import betteradvancements.platform.IPlatformHelper;

public class ForgePlatformHelper implements IPlatformHelper {
    private final ForgeEventHelper eventHelper = new ForgeEventHelper();
    private final ForgeAdvancementVisitor advancementVisitor = new ForgeAdvancementVisitor();

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public IEventHelper getEventHelper() {
        return eventHelper;
    }

    @Override
    public ForgeAdvancementVisitor getAdvancementVisitor() {
        return advancementVisitor;
    }
}
