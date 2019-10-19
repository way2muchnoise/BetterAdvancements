package betteradvancements.handler;

import betteradvancements.gui.BetterAdvancementsScreen;
import betteradvancements.gui.BetterAdvancementsScreenButtonWidget;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class GuiOpenHandler {
    public static final GuiOpenHandler instance = new GuiOpenHandler();

    private GuiOpenHandler() {

    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof AdvancementsScreen) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getInstance();
            mc.displayGuiScreen(new BetterAdvancementsScreen(mc.player.connection.getAdvancementManager()));
        }
    }

    @SubscribeEvent
    public void onGuiOpened(final GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof InventoryScreen) {
            if (BetterAdvancementsScreenButtonWidget.addToInventory) {
                InventoryScreen guiInventory = (InventoryScreen) event.getGui();
                event.addWidget(new BetterAdvancementsScreenButtonWidget(guiInventory.getGuiLeft() + guiInventory.getXSize(), guiInventory.getGuiTop(), "BA"));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) // put on HIGH to be before Triumph sorting, giving them priority
    public void onGuiAboutToOpen(final GuiScreenEvent.InitGuiEvent.Pre event) {
        if (event.getGui() instanceof BetterAdvancementsScreen) {
            if (BetterAdvancementsScreen.orderTabsAlphabetically) {
                Minecraft mc = Minecraft.getInstance();
                ClientAdvancementManager manager = mc.player.connection.getAdvancementManager();
                AdvancementList advancementList = manager.getAdvancementList();
                Set<Advancement> roots = (Set<Advancement>) advancementList.getRoots();

                List<String> advancementLocations = roots.stream().sorted(Comparator.comparing(a -> a.getDisplayText().getUnformattedComponentText().toLowerCase())).map(a -> a.getId().toString()).collect(Collectors.toList());

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
