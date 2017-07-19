package betteradvancements.reference;

import net.minecraft.util.ResourceLocation;

public final class Resources {
    private static ResourceLocation resourceLocation(String location) {
        return new ResourceLocation(Reference.ID, location);
    }

    public static final class Gui {
        public static final ResourceLocation WINDOW = resourceLocation(Textures.Gui.WINDOW);
        public static final ResourceLocation TABS = resourceLocation(Textures.Gui.TABS);
        public static final ResourceLocation WIDGETS = resourceLocation(Textures.Gui.WIDGETS);
    }
}
