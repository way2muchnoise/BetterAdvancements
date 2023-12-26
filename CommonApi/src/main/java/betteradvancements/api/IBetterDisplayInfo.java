package betteradvancements.api;

import net.minecraft.resources.ResourceLocation;

/**
 * More advanced information for displaying advancements
 * Can be implemented on an {@link net.minecraft.advancements.Advancement} or a {@link net.minecraft.advancements.DisplayInfo}
 */
public interface IBetterDisplayInfo {
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

    /**
     * If the advancement should use direct lines for its connections
     *
     * @return a boolean value or null to default
     */
    default Boolean drawDirectLines() {
        return null;
    }

    /**
     * The inner color of the connection line when an advancement is completed
     *
     * @return an integer color value or -1 to default
     */
    default int getCompletedLineColor() {
        return -1;
    }

    /**
     * The inner color of the connection line when an advancement is uncompleted
     *
     * @return an integer color value or -1 to default
     */
    default int getUnCompletedLineColor() {
        return -1;
    }

    /**
     * The X position of the advancement, in pixels
     *
     * @return an integer position or null to default
     */
    default Integer getPosX() {
        return null;
    }

    /**
     * The Y position of the advancement, in pixels
     *
     * @return an integer position or null to default
     */
    default Integer getPosY() {
        return null;
    }

    /**
     * If the advancement should hide its connection lines
     *
     * @return a boolean value or null to default
     */
    default Boolean hideLines() {
        return null;
    }

    /**
     * If the advancement should be able to be dragged in the gui
     *
     * @return a boolean value
     */
    default boolean allowDragging() {
        return false;
    }
}
