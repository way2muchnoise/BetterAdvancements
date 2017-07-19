package betteradvancements.handler;

import betteradvancements.gui.GuiScreenBetterAdvancements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiOpenHandler {
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiScreenAdvancements) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(new GuiScreenBetterAdvancements(mc.player.connection.getAdvancementManager()));
        }
    }
}
