package betteradvancements.gui;

import betteradvancements.reference.Resources;
import betteradvancements.util.RenderUtil;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiScreenBetterAdvancements extends GuiScreen implements ClientAdvancementManager.IListener {
    private static final int WIDTH = 252, HEIGHT = 140, CORNER_SIZE = 30;
    private static final int SIDE = 20, TOP = 40, PADDING = 9;
    private final ClientAdvancementManager clientAdvancementManager;
    private final Map<Advancement, GuiBetterAdvancementTab> tabs = Maps.newLinkedHashMap();
    private GuiBetterAdvancementTab selectedTab;
    private int scrollMouseX;
    private int scrollMouseY;
    private boolean isScrolling;

    public GuiScreenBetterAdvancements(ClientAdvancementManager clientAdvancementManager) {
        this.clientAdvancementManager = clientAdvancementManager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.tabs.clear();
        this.selectedTab = null;
        this.clientAdvancementManager.setListener(this);

        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.clientAdvancementManager.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
        } else {
            this.clientAdvancementManager.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        this.clientAdvancementManager.setListener(null);
        NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();

        if (nethandlerplayclient != null) {
            nethandlerplayclient.sendPacket(CPacketSeenAdvancements.closedScreen());
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (GuiBetterAdvancementTab guiBetterAdvancementTab : this.tabs.values()) {
                if (guiBetterAdvancementTab.isMouseOver(SIDE, TOP, mouseX, mouseY)) {
                    this.clientAdvancementManager.setSelectedTab(guiBetterAdvancementTab.getAdvancement(), true);
                    break;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == this.mc.gameSettings.keyBindAdvancements.getKeyCode()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Mouse.isButtonDown(0)) {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.scroll(mouseX - this.scrollMouseX, mouseY - this.scrollMouseY, width - 2*SIDE - 2*PADDING, height - TOP - SIDE - 3*PADDING);
            }

            this.scrollMouseX = mouseX;
            this.scrollMouseY = mouseY;
        } else {
            this.isScrolling = false;
        }

        this.drawDefaultBackground();
        this.renderInside(mouseX, mouseY, SIDE, TOP, width - SIDE, height - SIDE);
        this.renderWindow(SIDE, TOP, width - SIDE, height - SIDE);
        this.renderToolTips(mouseX, mouseY, SIDE, TOP, width - SIDE, height - SIDE);
    }

    private void renderInside(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        GuiBetterAdvancementTab guiBetterAdvancementTab = this.selectedTab;
        int boxLeft = left + PADDING;
        int boxTop = top + 2*PADDING;
        int boxRight = right - PADDING;
        int boxBottom = bottom - PADDING;

        if (guiBetterAdvancementTab == null) {
            drawRect(boxLeft, boxTop, boxRight, boxBottom, -16777216);
            String s = I18n.format("advancements.empty");
            int i = this.fontRenderer.getStringWidth(s);
            this.fontRenderer.drawString(s, boxLeft + 117 - i / 2, boxTop + 56 - this.fontRenderer.FONT_HEIGHT / 2, -1);
            this.fontRenderer.drawString(":(", boxLeft + 117 - this.fontRenderer.getStringWidth(":(") / 2, boxTop + 113 - this.fontRenderer.FONT_HEIGHT, -1);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (boxLeft), (float) (boxTop), -400.0F);
            GlStateManager.enableDepth();
            guiBetterAdvancementTab.drawContents(boxRight - boxLeft, boxBottom - boxTop);
            GlStateManager.popMatrix();
            GlStateManager.depthFunc(515);
            GlStateManager.disableDepth();
        }
    }

    public void renderWindow(int left, int top, int right, int bottom) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
        this.mc.getTextureManager().bindTexture(Resources.Gui.WINDOW);
        // Top left corner
        this.drawTexturedModalRect(left, top, 0, 0, CORNER_SIZE, CORNER_SIZE);
        // Top side
        RenderUtil.renderRepeating(this, left + CORNER_SIZE, TOP, width - left - CORNER_SIZE - SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, 0, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Top right corner
        this.drawTexturedModalRect(right - CORNER_SIZE, top, WIDTH - CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE);
        // Left side
        RenderUtil.renderRepeating(this, left, top + CORNER_SIZE, CORNER_SIZE, bottom - top - CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Right side
        RenderUtil.renderRepeating(this, right - CORNER_SIZE, top + CORNER_SIZE, CORNER_SIZE, bottom - top - CORNER_SIZE, WIDTH - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Bottom left corner
        this.drawTexturedModalRect(left, bottom - CORNER_SIZE, 0, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);
        // Bottom side
        RenderUtil.renderRepeating(this, left + CORNER_SIZE, bottom - CORNER_SIZE, width - left - CORNER_SIZE - SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Bottom right corner
        this.drawTexturedModalRect(right - CORNER_SIZE, bottom - CORNER_SIZE, WIDTH - CORNER_SIZE, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);

        if (this.tabs.size() > 1) {
            this.mc.getTextureManager().bindTexture(Resources.Gui.TABS);

            for (GuiBetterAdvancementTab tab : this.tabs.values()) {
                tab.drawTab(left, top, tab == this.selectedTab);
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (GuiBetterAdvancementTab tab : this.tabs.values()) {
                tab.drawIcon(left, top, this.itemRender);
            }

            GlStateManager.disableBlend();
        }

        this.fontRenderer.drawString(I18n.format("gui.advancements"), left + 8, top + 6, 4210752);
    }

    private void renderToolTips(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.selectedTab != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.translate((float) (left + PADDING), (float) (top + 2*PADDING), 400.0F);
            this.selectedTab.drawToolTips(mouseX - left - PADDING, mouseY - top - 2*PADDING, left, top, right - left - 2*PADDING, bottom - top - 3*PADDING);
            GlStateManager.disableDepth();
            GlStateManager.popMatrix();
        }

        if (this.tabs.size() > 1) {
            for (GuiBetterAdvancementTab tab : this.tabs.values()) {
                if (tab.isMouseOver(left, top, mouseX, mouseY)) {
                    this.drawHoveringText(tab.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    public void rootAdvancementAdded(@Nonnull Advancement advancement) {
        GuiBetterAdvancementTab guiBetterAdvancementTab = GuiBetterAdvancementTab.create(this.mc, this, this.tabs.size(), advancement);

        if (guiBetterAdvancementTab != null) {
            this.tabs.put(advancement, guiBetterAdvancementTab);
        }
    }

    public void rootAdvancementRemoved(@Nonnull Advancement advancementIn) {
    }

    public void nonRootAdvancementAdded(@Nonnull Advancement advancementIn) {
        GuiBetterAdvancementTab guiBetterAdvancementTab = this.getTab(advancementIn);

        if (guiBetterAdvancementTab != null) {
            guiBetterAdvancementTab.addAdvancement(advancementIn);
        }
    }

    public void nonRootAdvancementRemoved(@Nonnull Advancement advancementIn) {
    }

    public void onUpdateAdvancementProgress(@Nonnull Advancement advancement, @Nonnull AdvancementProgress advancementProgress) {
        GuiBetterAdvancement guiBetterAdvancement = this.getAdvancementGui(advancement);

        if (guiBetterAdvancement != null) {
            guiBetterAdvancement.getAdvancementProgress(advancementProgress);
        }
    }

    public void setSelectedTab(@Nullable Advancement advancement) {
        this.selectedTab = this.tabs.get(advancement);
    }

    public void advancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public GuiBetterAdvancement getAdvancementGui(Advancement advancement) {
        GuiBetterAdvancementTab guiBetterAdvancementTab = this.getTab(advancement);
        return guiBetterAdvancementTab == null ? null : guiBetterAdvancementTab.getAdvancementGui(advancement);
    }

    @Nullable
    private GuiBetterAdvancementTab getTab(@Nonnull Advancement advancement) {
        while (advancement.getParent() != null) {
            advancement = advancement.getParent();
        }

        return this.tabs.get(advancement);
    }
}