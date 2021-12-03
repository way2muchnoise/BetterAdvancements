package betteradvancements.handler;

import betteradvancements.gui.BetterAdvancementsScreen;
import betteradvancements.gui.BetterAdvancementsScreenButton;
import betteradvancements.util.AdvancementComparer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class GuiOpenHandler {
    public static final GuiOpenHandler instance = new GuiOpenHandler();

    private GuiOpenHandler() {

    }

    @SubscribeEvent
    public void onGuiOpen(ScreenOpenEvent event) {
        if (event.getScreen() instanceof AdvancementsScreen) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new BetterAdvancementsScreen(mc.player.connection.getAdvancements()));
        }
    }

    @SubscribeEvent
    public void onGuiOpened(final ScreenEvent.InitScreenEvent.Post event) {
        if (event.getScreen() instanceof InventoryScreen guiInventory) {
            if (BetterAdvancementsScreenButton.addToInventory) {
                event.addListener(new BetterAdvancementsScreenButton(guiInventory.getGuiLeft() + guiInventory.getXSize(), guiInventory.getGuiTop(), new TextComponent("BA")));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) // put on HIGH to be before Triumph sorting, giving them priority
    public void onGuiAboutToOpen(final ScreenEvent.InitScreenEvent.Pre event) {
        if (event.getScreen() instanceof BetterAdvancementsScreen) {
            if (BetterAdvancementsScreen.orderTabsAlphabetically) {
                Minecraft mc = Minecraft.getInstance();
                ClientAdvancements clientAdvancements = mc.player.connection.getAdvancements();
                AdvancementList advancementList = clientAdvancements.getAdvancements();
                Set<Advancement> roots = (Set<Advancement>) advancementList.getRoots();

                List<String> advancementLocations = roots.stream().sorted(AdvancementComparer.sortByTitle()).map(a -> a.getId().toString()).collect(Collectors.toList());

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
