package betteradvancements.api;

import net.minecraft.util.ResourceLocation;

/**
 * More advanced information for displaying advancements
 * Can be implemented on an {@link net.minecraft.advancements.Advancement} or a {@link net.minecraft.advancements.DisplayInfo}
 */
public interface IBetterDisplayInfo {
    /**
     * @return The resource location this information is about
     */
    ResourceLocation getId();

    /**
     * The background color of the icon when an advancement is completed
     *
     * @return an integer color value or -1 to default
     */
    default int getCompletedIconColor() {
        return -1;
    }

    /**
     * The background color of the icon when an advancement is uncompleted
     *
     * @return an integer color value or -1 to default
     */
    default int getUnCompletedIconColor() {
        return -1;
    }

    /**
     * The background color of the title text when an advancement is completed
     *
     * @return an integer color value or -1 to default
     */
    default int getCompletedTitleColor() {
        return -1;
    }

    /**
     * The background color of the title text when an advancement is uncompleted
     *
     * @return an integer color value or -1 to default
     */
    default int getUnCompletedTitleColor() {
        return -1;
    }
}
