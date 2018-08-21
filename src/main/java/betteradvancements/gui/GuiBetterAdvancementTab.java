package betteradvancements.gui;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.advancements.BetterDisplayInfoRegistry;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiBetterAdvancementTab extends Gui {
    public static boolean doFade = true;

    private final Minecraft minecraft;
    private final GuiScreenBetterAdvancements screen;
    private final BetterAdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final String title;
    private final GuiBetterAdvancement root;
    private final Map<Advancement, GuiBetterAdvancement> guis = Maps.newLinkedHashMap();
    private final BetterDisplayInfoRegistry betterDisplayInfos;

    protected int scrollX, scrollY;
    private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
    private int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public GuiBetterAdvancementTab(Minecraft mc, GuiScreenBetterAdvancements guiScreenBetterAdvancements, BetterAdvancementTabType type, int index, Advancement advancement, DisplayInfo displayInfo) {
        this.minecraft = mc;
        this.screen = guiScreenBetterAdvancements;
        this.type = type;
        this.index = index;
        this.advancement = advancement;
        this.display = displayInfo;
        this.icon = displayInfo.getIcon();
        this.title = displayInfo.getTitle().getFormattedText();
        this.betterDisplayInfos = new BetterDisplayInfoRegistry(advancement);
        this.root = new GuiBetterAdvancement(this, mc, advancement, displayInfo);
        this.addGuiAdvancement(this.root, advancement);
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public String getTitle() {
        return this.title;
    }

    public void drawTab(int left, int top, int width, int height, boolean selected) {
        this.type.draw(this, left, top, width, height, selected, this.index);
    }

    public void drawIcon(int left, int top,int width, int height, RenderItem renderItem) {
        this.type.drawIcon(left, top, width, height, this.index, renderItem, this.icon);
    }

    public void drawContents(int width, int height) {
        if (!this.centered) {
            this.scrollX = (width - (this.maxX + this.minX)) / 2;
            this.scrollY = (height - (this.maxY + this.minY)) / 2;
            this.centered = true;
        }

        GlStateManager.depthFunc(518);
        drawRect(0, 0, width, height, -16777216);
        GlStateManager.depthFunc(515);
        ResourceLocation resourcelocation = this.display.getBackground();

        if (resourcelocation != null) {
            this.minecraft.getTextureManager().bindTexture(resourcelocation);
        } else {
            this.minecraft.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.scrollX % 16;
        int j = this.scrollY % 16;

        for (int k = -1; k <= width / 16; k++) {
            int l = -1;
            for (;l <= height / 16; l++) {
                drawModalRectWithCustomSizedTexture(i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
            }
            drawModalRectWithCustomSizedTexture(i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, height % 16, 16.0F, 16.0F);
        }

        this.root.drawConnectivity(this.scrollX, this.scrollY, true);
        this.root.drawConnectivity(this.scrollX, this.scrollY, false);
        this.root.draw(this.scrollX, this.scrollY);
    }

    public void drawToolTips(int mouseX, int mouseY, int left, int top, int width, int height) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 200.0F);
        drawRect(0, 0, width, height, MathHelper.floor(this.fade * 255.0F) << 24);
        boolean flag = false;

        if (mouseX > 0 && mouseX < width && mouseY > 0 && mouseY < height) {
            for (GuiBetterAdvancement guiBetterAdvancement : this.guis.values()) {
                if (guiBetterAdvancement.isMouseOver(this.scrollX, this.scrollY, mouseX, mouseY)) {
                    flag = true;
                    guiBetterAdvancement.drawHover(this.scrollX, this.scrollY, this.fade, left, top);
                    break;
                }
            }
        }

        GlStateManager.popMatrix();

        if (doFade && flag) {
            this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public boolean isMouseOver(int left, int top, int width, int height, int mouseX, int mouseY) {
        return this.type.isMouseOver(left, top, width, height, this.index, mouseX, mouseY);
    }

    @Nullable
    public static GuiBetterAdvancementTab create(Minecraft mc, GuiScreenBetterAdvancements guiScreenBetterAdvancements, int index, Advancement advancement, int width, int height) {
        if (advancement.getDisplay() == null) {
            return null;
        } else {
            BetterAdvancementTabType advancementTabType = BetterAdvancementTabType.getTabType(width,height, index);
            if (advancementTabType == null) {
                return null;
            } else {
                return new GuiBetterAdvancementTab(mc, guiScreenBetterAdvancements, advancementTabType, index, advancement, advancement.getDisplay());
            }

        }
    }

    public void scroll(int scrollX, int scrollY, int width, int height) {
        if (this.maxX - this.minX > width) {
            this.scrollX = MathHelper.clamp(this.scrollX + scrollX, -(this.maxX - width), 0);
        }

        if (this.maxY - this.minY > height) {
            this.scrollY = MathHelper.clamp(this.scrollY + scrollY, -(this.maxY - height), 0);
        }
    }

    public void addAdvancement(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            GuiBetterAdvancement guiBetterAdvancement = new GuiBetterAdvancement(this, this.minecraft, advancement, advancement.getDisplay());
            this.addGuiAdvancement(guiBetterAdvancement, advancement);
        }
    }

    private void addGuiAdvancement(GuiBetterAdvancement guiBetterAdvancement, Advancement advancement) {
        this.guis.put(advancement, guiBetterAdvancement);
        int left = guiBetterAdvancement.getX();
        int right = left + 28;
        int top = guiBetterAdvancement.getY();
        int bottom = top + 27;
        this.minX = Math.min(this.minX, left);
        this.maxX = Math.max(this.maxX, right);
        this.minY = Math.min(this.minY, top);
        this.maxY = Math.max(this.maxY, bottom);

        for (GuiBetterAdvancement gui : this.guis.values()) {
            gui.attachToParent();
        }
    }

    @Nullable
    public GuiBetterAdvancement getAdvancementGui(Advancement advancement) {
        return this.guis.get(advancement);
    }

    public GuiScreenBetterAdvancements getScreen() {
        return this.screen;
    }

    public BetterDisplayInfo getBetterDisplayInfo(Advancement advancement) {
        return betterDisplayInfos.get(advancement);
    }
}
