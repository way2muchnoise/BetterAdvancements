package betteradvancements.platform;

public interface IPlatformHelper {
    String getPlatformName();

    IEventHelper getEventHelper();

    IAdvancementVisitor getAdvancementVisitor();
}
