package betteradvancements.gui;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.advancements.BetterDisplayInfoRegistry;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class BetterAdvancementTab {
    public static boolean doFade = true;
    public static final Map<Advancement, Tuple<Integer, Integer>> scrollHistory = Maps.newLinkedHashMap();

    private final Minecraft minecraft;
    private final BetterAdvancementsScreen screen;
    private final BetterAdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final Component title;
    private final BetterAdvancementWidget root;
    protected final Map<Advancement, BetterAdvancementWidget> guis = Maps.newLinkedHashMap();
    private final BetterDisplayInfoRegistry betterDisplayInfos;

    protected int scrollX, scrollY;
    private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
    private int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public BetterAdvancementTab(Minecraft mc, BetterAdvancementsScreen betterAdvancementsScreen, BetterAdvancementTabType type, int index, Advancement advancement, DisplayInfo displayInfo) {
        this.minecraft = mc;
        this.screen = betterAdvancementsScreen;
        this.type = type;
        this.index = index;
        this.advancement = advancement;
        this.display = displayInfo;
        this.icon = displayInfo.getIcon();
        this.title = displayInfo.getTitle();
        this.betterDisplayInfos = new BetterDisplayInfoRegistry(advancement);
        this.root = new BetterAdvancementWidget(this, mc, advancement, displayInfo);
        this.addGuiAdvancement(this.root, advancement);
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public Component getTitle() {
        return this.title;
    }

    public void drawTab(GuiGraphics guiGraphics, int left, int top, int width, int height, boolean selected) {
        this.type.draw(guiGraphics, left, top, width, height, selected, this.index);
    }

    public void drawIcon(GuiGraphics guiGraphics, int left, int top,int width, int height) {
        this.type.drawIcon(guiGraphics, left, top, width, height, this.index, this.icon);
    }

    public void drawContents(GuiGraphics guiGraphics, int left, int top, int width, int height) {
        if (!this.centered) {
            this.scrollX = (width - (this.maxX + this.minX)) / 2;
            this.scrollY = (height - (this.maxY + this.minY)) / 2;
            this.centered = true;
        }

        guiGraphics.enableScissor(left, top, left + width, top + height);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(left, top, 0);
        ResourceLocation resourcelocation = Objects.requireNonNullElse(this.display.getBackground(), TextureManager.INTENTIONAL_MISSING_TEXTURE);

        int i = this.scrollX % 16;
        int j = this.scrollY % 16;

        int k = -1;
        for (; k <= 1 + width / 16; k++) {
            int l = -1;
            for (;l <= height / 16; l++) {
                guiGraphics.blit(resourcelocation, i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, 16, 16, 16);
            }
            guiGraphics.blit(resourcelocation, i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, height % 16, 16, 16);
        }


        this.root.drawConnectivity(guiGraphics, this.scrollX, this.scrollY, true);
        this.root.drawConnectivity(guiGraphics, this.scrollX, this.scrollY, false);
        this.root.draw(guiGraphics, this.scrollX, this.scrollY);
        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();
    }

    public void drawToolTips(GuiGraphics guiGraphics, int mouseX, int mouseY, int left, int top, int width, int height) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0D, 0.0D, -200.0D);
        guiGraphics.fill(0, 0, width, height, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;

        if (mouseX > 0 && mouseX < width && mouseY > 0 && mouseY < height) {
            for (BetterAdvancementWidget betterAdvancementWidget : this.guis.values()) {
                if (betterAdvancementWidget.isMouseOver(this.scrollX, this.scrollY, mouseX, mouseY)) {
                    flag = true;
                    betterAdvancementWidget.drawHover(guiGraphics, this.scrollX, this.scrollY, this.fade, left, top);
                    break;
                }
            }
        }

        guiGraphics.pose().popPose();

        if (doFade && flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public boolean isMouseOver(int left, int top, int width, int height, double mouseX, double mouseY) {
        return this.type.isMouseOver(left, top, width, height, this.index, mouseX, mouseY);
    }

    @Nullable
    public static BetterAdvancementTab create(Minecraft mc, BetterAdvancementsScreen betterAdvancementsScreen, int index, Advancement advancement, int width, int height) {
        if (advancement.getDisplay() == null) {
            return null;
        } else {
            BetterAdvancementTabType advancementTabType = BetterAdvancementTabType.getTabType(width, height, index);
            if (advancementTabType == null) {
                return null;
            } else {
                return new BetterAdvancementTab(mc, betterAdvancementsScreen, advancementTabType, index, advancement, advancement.getDisplay());
            }

        }
    }

    public void scroll(double scrollX, double scrollY, int width, int height) {
        if (this.maxX - this.minX > width) {
            this.scrollX = (int)Math.round(Mth.clamp(this.scrollX + scrollX, -(this.maxX - width), -this.minX));
        }

        if (this.maxY - this.minY > height) {
            this.scrollY = (int)Math.round(Mth.clamp(this.scrollY + scrollY, -(this.maxY - height), -this.minY));
        }
    }

    public void addAdvancement(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            BetterAdvancementWidget betterAdvancementEntryScreen = new BetterAdvancementWidget(this, this.minecraft, advancement, advancement.getDisplay());
            this.addGuiAdvancement(betterAdvancementEntryScreen, advancement);
        }
    }

    private void addGuiAdvancement(BetterAdvancementWidget betterAdvancementEntryScreen, Advancement advancement) {
        this.guis.put(advancement, betterAdvancementEntryScreen);
        int left = betterAdvancementEntryScreen.getX();
        int right = left + 28;
        int top = betterAdvancementEntryScreen.getY();
        int bottom = top + 27;
        this.minX = Math.min(this.minX, left);
        this.maxX = Math.max(this.maxX, right);
        this.minY = Math.min(this.minY, top);
        this.maxY = Math.max(this.maxY, bottom);

        for (BetterAdvancementWidget gui : this.guis.values()) {
            gui.attachToParent();
        }
    }

    @Nullable
    public BetterAdvancementWidget getWidget(Advancement advancement) {
        return this.guis.get(advancement);
    }

    public BetterAdvancementsScreen getScreen() {
        return this.screen;
    }

    public BetterDisplayInfo getBetterDisplayInfo(Advancement advancement) {
        return betterDisplayInfos.get(advancement);
    }

    public void storeScroll() {
        scrollHistory.put(this.advancement, new Tuple<>(scrollX, scrollY));
    }

    public void loadScroll() {
        Tuple<Integer, Integer> scroll = scrollHistory.get(this.advancement);
        if (scroll != null) {
            this.centered = true;
            this.scrollX = scroll.getA();
            this.scrollY = scroll.getB();
        }
    }
}
