package betteradvancements.neoforge.handler;

import betteradvancements.common.gui.BetterAdvancementsScreen;
import betteradvancements.common.gui.BetterAdvancementsScreenButton;
import betteradvancements.common.util.AdvancementComparer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class GuiOpenHandler {
    public static final GuiOpenHandler instance = new GuiOpenHandler();

    private GuiOpenHandler() {

    }

    @SubscribeEvent
    public void onGuiOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof AdvancementsScreen) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new BetterAdvancementsScreen(mc.player.connection.getAdvancements()));
        }
    }

    @SubscribeEvent
    public void onGuiOpened(final ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen) {
            if (BetterAdvancementsScreenButton.addToInventory) {
                InventoryScreen guiInventory = (InventoryScreen) event.getScreen();
                event.addListener(new BetterAdvancementsScreenButton(guiInventory.getGuiLeft() + guiInventory.getXSize(), guiInventory.getGuiTop(), Component.literal("BA")));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) // put on HIGH to be before Triumph sorting, giving them priority
    public void onGuiAboutToOpen(final ScreenEvent.Init.Pre event) {
        if (event.getScreen() instanceof BetterAdvancementsScreen) {
            if (BetterAdvancementsScreen.orderTabsAlphabetically) {
                Minecraft mc = Minecraft.getInstance();
                ClientAdvancements clientAdvancements = mc.player.connection.getAdvancements();
                AdvancementList advancementTree = clientAdvancements.getAdvancements();
                Set<Advancement> roots = (Set<Advancement>) advancementTree.getRoots();

                List<String> advancementLocations = roots.stream().sorted(AdvancementComparer.sortByTitle()).map(n -> n.getId().toString()).toList();

                List<Advancement> advancements = new ArrayList<>(roots);
                roots.clear();

                for (String location : advancementLocations) {
                    for (Advancement advancement : advancements) {
                        if (advancement.getId().toString().equals(location)) {
                            roots.add(advancement);
                        }
                    }
                }
            }
        }
    }
}
