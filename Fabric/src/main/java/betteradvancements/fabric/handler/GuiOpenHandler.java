package betteradvancements.fabric.handler;

import betteradvancements.common.gui.BetterAdvancementsScreenButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

public class GuiOpenHandler implements ScreenEvents.AfterInit {
    public static final GuiOpenHandler instance = new GuiOpenHandler();

    private GuiOpenHandler() {

    }

    public void registerEventHandlers() {
        ScreenEvents.AFTER_INIT.register(this);
    }

    @Override
    public void afterInit(Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof InventoryScreen) {
            if (BetterAdvancementsScreenButton.addToInventory) {
                InventoryScreen inventoryScreen = (InventoryScreen) screen;
                Screens.getButtons(screen).add(new BetterAdvancementsScreenButton(inventoryScreen.leftPos + inventoryScreen.imageWidth, inventoryScreen.topPos, Component.literal("BA")));
            }
        }
    }
}
