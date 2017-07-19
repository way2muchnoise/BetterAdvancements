package betteradvancements.gui;

import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.advancements.AdvancementTabType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiBetterAdvancementTab extends Gui {
    private final Minecraft minecraft;
    private final GuiScreenBetterAdvancements screen;
    private final AdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final String title;
    private final GuiBetterAdvancement root;
    private final Map<Advancement, GuiBetterAdvancement> guis = Maps.newLinkedHashMap();

    private int scrollX, scrollY;
    private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
    private int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public GuiBetterAdvancementTab(Minecraft mc, GuiScreenBetterAdvancements guiScreenBetterAdvancements, AdvancementTabType type, int index, Advancement advancement, DisplayInfo displayInfo) {
        this.minecraft = mc;
        this.screen = guiScreenBetterAdvancements;
        this.type = type;
        this.index = index;
        this.advancement = advancement;
        this.display = displayInfo;
        this.icon = displayInfo.getIcon();
        this.title = displayInfo.getTitle().getFormattedText();
        this.root = new GuiBetterAdvancement(this, mc, advancement, displayInfo);
        this.addGuiAdvancement(this.root, advancement);
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public String getTitle() {
        return this.title;
    }

    public void drawTab(int left, int top, boolean selected) {
        this.type.draw(this, left, top, selected, this.index);
    }

    public void drawIcon(int left, int top, RenderItem renderItem) {
        this.type.drawIcon(left, top, this.index, renderItem, this.icon);
    }

    public void drawContents(int width, int height) {
        if (!this.centered) {
            this.scrollX = width - (this.maxX + this.minX) / 2;
            this.scrollY = height - (this.maxY + this.minY) / 2;
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

        for (int k = -1; k <= 15; ++k) {
            for (int l = -1; l <= 8; ++l) {
                drawModalRectWithCustomSizedTexture(i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
            }
        }

        this.root.drawConnectivity(this.scrollX, this.scrollY, true);
        this.root.drawConnectivity(this.scrollX, this.scrollY, false);
        this.root.draw(this.scrollX, this.scrollY);
    }

    public void drawToolTips(int mouseX, int mouseY, int left, int top) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 200.0F);
        drawRect(0, 0, 234, 113, MathHelper.floor(this.fade * 255.0F) << 24);
        boolean flag = false;

        if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113) {
            for (GuiBetterAdvancement guiBetterAdvancement : this.guis.values()) {
                if (guiBetterAdvancement.isMouseOver(this.scrollX, this.scrollY, mouseX, mouseY)) {
                    flag = true;
                    guiBetterAdvancement.drawHover(this.scrollX, this.scrollY, this.fade, left, top);
                    break;
                }
            }
        }

        GlStateManager.popMatrix();

        if (flag) {
            this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public boolean isMouseOver(int left, int top, int mouseX, int mouseY) {
        return this.type.isMouseOver(left, top, this.index, mouseX, mouseY);
    }

    @Nullable
    public static GuiBetterAdvancementTab create(Minecraft mc, GuiScreenBetterAdvancements guiScreenBetterAdvancements, int index, Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return null;
        } else {
            for (AdvancementTabType advancementtabtype : AdvancementTabType.values()) {
                if (index < advancementtabtype.getMax()) {
                    return new GuiBetterAdvancementTab(mc, guiScreenBetterAdvancements, advancementtabtype, index, advancement, advancement.getDisplay());
                }

                index -= advancementtabtype.getMax();
            }

            return null;
        }
    }

    public void scroll(int scrollX, int scrollY) {
        if (this.maxX - this.minX > 234) {
            this.scrollX = MathHelper.clamp(this.scrollX + scrollX, -(this.maxX - 234), 0);
        }

        if (this.maxY - this.minY > 113) {
            this.scrollY = MathHelper.clamp(this.scrollY + scrollY, -(this.maxY - 113), 0);
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
        int i = guiBetterAdvancement.getX();
        int j = i + 28;
        int k = guiBetterAdvancement.getY();
        int l = k + 27;
        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);

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
}
