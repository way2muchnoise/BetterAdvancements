package betteradvancements.common.platform;

public interface IPlatformHelper {
    String getPlatformName();

    IEventHelper getEventHelper();

    IAdvancementVisitor getAdvancementVisitor();
}
