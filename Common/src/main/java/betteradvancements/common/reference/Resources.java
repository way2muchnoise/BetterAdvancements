package betteradvancements.common.reference;

import net.minecraft.resources.Identifier;

public final class Resources {
    private static Identifier resourceLocation(String location) {
        return Identifier.fromNamespaceAndPath(Constants.ID, location);
    }

    public static final class Gui {
        public static final Identifier WINDOW = resourceLocation(Textures.Gui.WINDOW);
        public static final Identifier TABS = resourceLocation(Textures.Gui.TABS);
        public static final Identifier WIDGETS = resourceLocation(Textures.Gui.WIDGETS);
    }
}
