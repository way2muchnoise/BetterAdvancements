package betteradvancements.gui;

import betteradvancements.api.event.AdvancementMovedEvent;
import betteradvancements.reference.Resources;
import betteradvancements.util.RenderUtil;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class GuiScreenBetterAdvancements extends Screen implements ClientAdvancementManager.IListener {
    private static final int WIDTH = 252, HEIGHT = 140, CORNER_SIZE = 30;
    private static final int SIDE = 30, TOP = 40, BOTTOM = 30, PADDING = 9;
    private static final float MIN_ZOOM = 1, MAX_ZOOM = 2, ZOOM_STEP = 0.2F;
    private final ClientAdvancementManager clientAdvancementManager;
    private final Map<Advancement, GuiBetterAdvancementTab> tabs = Maps.newLinkedHashMap();
    private GuiBetterAdvancementTab selectedTab;
    private int scrollMouseX, scrollMouseY;
    private float zoom = MIN_ZOOM;
    private boolean isScrolling;
    protected int internalWidth, internalHeight;
    public static int uiScaling;
    public static boolean showDebugCoordinates = false;
    public static boolean orderTabsAlphabetically = false;
    private BetterAdvancementEntryGui advConnectedToMouse = null;

    public GuiScreenBetterAdvancements(ClientAdvancementManager clientAdvancementManager) {
        super(NarratorChatListener.field_216868_a);
        this.clientAdvancementManager = clientAdvancementManager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    protected void init() {
        this.internalHeight = this.height * uiScaling / 100;
        this.internalWidth = this.width * uiScaling / 100;

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
    @Override
    public void removed() {
        this.clientAdvancementManager.setListener(null);
        ClientPlayNetHandler clientPlayNetHandler = this.minecraft.getConnection();
        if (clientPlayNetHandler != null) {
            clientPlayNetHandler.sendPacket(CSeenAdvancementsPacket.closedScreen());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
        if (modifiers == 0) {
            int left = SIDE + (width - internalWidth) / 2;
            int top = TOP + (height - internalHeight) / 2;
            for (GuiBetterAdvancementTab guiBetterAdvancementTab : this.tabs.values()) {
                if (guiBetterAdvancementTab.isMouseOver(left, top, internalWidth - 2*SIDE, internalHeight - top - BOTTOM, mouseX, mouseY)) {
                    this.clientAdvancementManager.setSelectedTab(guiBetterAdvancementTab.getAdvancement(), true);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        int wheel = (int) scroll;
        if (wheel < 0 && zoom > MIN_ZOOM) {
            zoom -= ZOOM_STEP;
        } else if (wheel > 0 && zoom < MAX_ZOOM) {
            zoom += ZOOM_STEP;
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == this.minecraft.gameSettings.keyBindAdvancements.getKey().getKeyCode()) {
            this.minecraft.displayGuiScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(int mouseX, int mouseY, float partialTicks) {
        int left = SIDE + (width - internalWidth) / 2;
        int top = TOP + (height - internalHeight) / 2;

        if (this.minecraft.mouseHelper.isLeftDown() && !this.isScrolling) {
            if (this.advConnectedToMouse == null) {
                boolean inGui = mouseX < left + internalWidth - 2*SIDE - PADDING && mouseX > left + PADDING && mouseY < top + internalHeight - TOP + 1 && mouseY > top + 2*PADDING;
                if (this.selectedTab != null && inGui) {
                    for (BetterAdvancementEntryGui betterAdvancementEntryGui : this.selectedTab.guis.values()) {
                        if (betterAdvancementEntryGui.isMouseOver(this.selectedTab.scrollX, this.selectedTab.scrollY, mouseX - left - PADDING, mouseY - top - 2*PADDING)) {
                            
                            if (betterAdvancementEntryGui.betterDisplayInfo.allowDragging())
                            {
                                this.advConnectedToMouse = betterAdvancementEntryGui;
                                break;
                            }
                        }
                    }
                }
            }
            else {
                int moveX = mouseX - this.scrollMouseX;
                int moveY = mouseY - this.scrollMouseY;
                
                this.advConnectedToMouse.x = this.advConnectedToMouse.x + moveX;
                this.advConnectedToMouse.y = this.advConnectedToMouse.y + moveY;
            }
        }
        else {
            if (this.advConnectedToMouse != null) {
                //Create and post event for the advancement movement
                final AdvancementMovedEvent event = new AdvancementMovedEvent(advConnectedToMouse);
                MinecraftForge.EVENT_BUS.post(event);
            }
            this.advConnectedToMouse = null;
        }
        
        if (this.minecraft.mouseHelper.isLeftDown()) {
            if (this.advConnectedToMouse == null) {
                if (!this.isScrolling) {
                    this.isScrolling = true;
                } else if (this.selectedTab != null) {
                    this.selectedTab.scroll(mouseX - this.scrollMouseX, mouseY - this.scrollMouseY, internalWidth - 2*SIDE - 3*PADDING, internalHeight - TOP - BOTTOM - 3*PADDING);
                }
            }
            this.scrollMouseX = mouseX;
            this.scrollMouseY = mouseY;
        } else {
            this.isScrolling = false;
        }

        int right = internalWidth - SIDE + (width - internalWidth) / 2;
        int bottom = internalHeight - SIDE + (height - internalHeight) / 2;

        this.renderBackground();
        this.renderInside(mouseX, mouseY, left, top, right, bottom);
        this.renderWindow(left, top, right, bottom);
        //Don't draw tool tips if dragging an advancement
        if (this.advConnectedToMouse == null) {
            this.renderToolTips(mouseX, mouseY, left, top, right, bottom);
        }
        
        //Draw guide lines to all advancements at 45 or 90 degree angles.
        if (this.advConnectedToMouse != null)
        {
            for (BetterAdvancementEntryGui betterAdvancementEntryGui : this.selectedTab.guis.values()) {
                if (betterAdvancementEntryGui != this.advConnectedToMouse)
                {
                    int x1 = betterAdvancementEntryGui.x + left + PADDING + this.selectedTab.scrollX + 3;
                    int x2 = this.advConnectedToMouse.x + left + PADDING + this.selectedTab.scrollX + 3;
                    int y1 = betterAdvancementEntryGui.y + top + 2 * PADDING + this.selectedTab.scrollY;
                    int y2 = this.advConnectedToMouse.y + top + 2 * PADDING + this.selectedTab.scrollY;
                    int centerX1 = betterAdvancementEntryGui.x + left + PADDING + this.selectedTab.scrollX + 3 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE / 2;
                    int centerX2 = this.advConnectedToMouse.x + left + PADDING + this.selectedTab.scrollX + 3 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE / 2;
                    int centerY1 = betterAdvancementEntryGui.y + top + 2 * PADDING + this.selectedTab.scrollY + BetterAdvancementEntryGui.ADVANCEMENT_SIZE / 2;
                    int centerY2 = this.advConnectedToMouse.y + top + 2 * PADDING + this.selectedTab.scrollY + BetterAdvancementEntryGui.ADVANCEMENT_SIZE / 2;
                    double degrees = Math.toDegrees(Math.atan2(centerX1 - centerX2, centerY1 - centerY2));
                    if (degrees < 0)
                    {
                        degrees += 360;
                    }
                    
                    if (betterAdvancementEntryGui.x == this.advConnectedToMouse.x)
                    {
                        if (y1 > y2)
                        {
                            //Draw right
                            RenderUtil.drawRect(x1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2, y2, 1, 0x00FF00);
                            //Draw bottom for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x2, y1, 1, 0x00FF00);
                            //Draw bottom for top
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for top
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2, x2, y2, 1, 0x00FF00);
                            //Draw left
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                        }
                        else
                        {
                            //Draw right
                            RenderUtil.drawRect(x1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2, y1, 1, 0x00FF00);
                            //Draw bottom for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2, x2, y2, 1, 0x00FF00);
                            //Draw bottom for top
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for top
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x2, y1, 1, 0x00FF00);
                            //Draw left
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, 1, 0x00FF00);
                        }
                    }
                    if (betterAdvancementEntryGui.y == this.advConnectedToMouse.y)
                    {
                        if (x1 > x2)
                        {
                            //Draw top
                            RenderUtil.drawRect(x2, y1, x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                            //Draw left for right
                            RenderUtil.drawRect(x1, y1, x1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for right
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw left for left
                            RenderUtil.drawRect(x2, y1, x2, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for left
                            RenderUtil.drawRect(x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw bottom
                            RenderUtil.drawRect(x2, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        }
                        else
                        {
                            //Draw left
                            RenderUtil.drawRect(x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x1, y2, 1, 0x00FF00);
                            //Draw left for right
                            RenderUtil.drawRect(x2, y1, x2, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for right
                            RenderUtil.drawRect(x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw left for left
                            RenderUtil.drawRect(x1, y1, x1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for left
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right
                            RenderUtil.drawRect(x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        }
                    }
                    if (degrees == 45 || degrees == 135 || degrees == 225 || degrees == 315)
                    {
                        //Draw lines around each advancement
                        //First
                        //Top
                        RenderUtil.drawRect(x1, y1, x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, 1, 0x00FF00);
                        //Bottom
                        RenderUtil.drawRect(x1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Left
                        RenderUtil.drawRect(x1, y1, x1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Right
                        RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Second
                        //Top
                        RenderUtil.drawRect(x2, y2, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                        //Bottom
                        RenderUtil.drawRect(x2, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Left
                        RenderUtil.drawRect(x2, y2, x2, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Right
                        RenderUtil.drawRect(x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        
                        if (degrees == 45 || degrees == 225)
                        {
                            RenderUtil.drawRect(x1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                        }
                        else if (degrees == 135 || degrees == 315)
                        {
                            RenderUtil.drawRect(x1, y1, x2, y2, 1, 0x00FF00);
                            RenderUtil.drawRect(x1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementEntryGui.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        }
                    }
                }
            }
        }

        if (GuiScreenBetterAdvancements.showDebugCoordinates && this.selectedTab != null && mouseX < internalWidth - SIDE - PADDING && mouseX > SIDE + PADDING && mouseY < internalHeight - top + 1 && mouseY > top + PADDING * 2) {
            //If dragging an advancement, draw coordinates of advancement being moved instead of mouse coordinates
            if (this.advConnectedToMouse != null) {
                //-3 and -1 are needed to have the coordinates be rendered where the advancement starts being rendered, rather than its real position.
                int currentX = this.advConnectedToMouse.x + left + PADDING + this.selectedTab.scrollX + 3 + 1;
                int currentY = this.advConnectedToMouse.y + top + 2 * PADDING + this.selectedTab.scrollY - font.FONT_HEIGHT + 1;

                font.drawString(this.advConnectedToMouse.x + "," + this.advConnectedToMouse.y, currentX, currentY, 0x000000);
            } else {
                //Draws a string containing the current position above the mouse. Locked to inside the advancement window.
                int xMouse = mouseX - left - PADDING;
                int yMouse = mouseY - top - 2 * PADDING;
                //-3 and -1 are needed to have the position be where the advancement starts being rendered, rather than its real position.
                int currentX = xMouse - this.selectedTab.scrollX - 3 - 1;
                int currentY = yMouse - this.selectedTab.scrollY - 1;

                font.drawString(currentX + "," + currentY, mouseX, mouseY - font.FONT_HEIGHT, 0x000000);
            }
        }
    }

    private void renderInside(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        GuiBetterAdvancementTab guiBetterAdvancementTab = this.selectedTab;
        int boxLeft = left + PADDING;
        int boxTop = top + 2*PADDING;
        int boxRight = right - PADDING;
        int boxBottom = bottom - PADDING;

        int width = boxRight - boxLeft;
        int height = boxBottom - boxTop;

        if (guiBetterAdvancementTab == null) {
            fill(boxLeft, boxTop, boxRight, boxBottom, -16777216);
            String s = I18n.format("advancements.empty");
            int i = this.font.getStringWidth(s);
            this.font.drawString(s, boxLeft + (width - i) / 2, boxTop + height / 2 - this.font.FONT_HEIGHT, -1);
            this.font.drawString(":(", boxLeft + (width - this.font.getStringWidth(":(")) / 2, boxTop + height / 2 + this.font.FONT_HEIGHT, -1);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translated((float) (boxLeft), (float) (boxTop), -400.0F);
            GlStateManager.enableDepthTest();
            guiBetterAdvancementTab.drawContents(width, height);
            GlStateManager.popMatrix();
            GlStateManager.depthFunc(515);
            GlStateManager.disableDepthTest();
        }
    }

    public void renderWindow(int left, int top, int right, int bottom) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
        this.minecraft.getTextureManager().bindTexture(Resources.Gui.WINDOW);
        // Top left corner
        this.blit(left, top, 0, 0, CORNER_SIZE, CORNER_SIZE);
        // Top side
        RenderUtil.renderRepeating(this, left + CORNER_SIZE, top, internalWidth - CORNER_SIZE - 2*SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, 0, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Top right corner
        this.blit(right - CORNER_SIZE, top, WIDTH - CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE);
        // Left side
        RenderUtil.renderRepeating(this, left, top + CORNER_SIZE, CORNER_SIZE, bottom - top - 2 * CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Right side
        RenderUtil.renderRepeating(this, right - CORNER_SIZE, top + CORNER_SIZE, CORNER_SIZE, bottom - top - 2 * CORNER_SIZE, WIDTH - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Bottom left corner
        this.blit(left, bottom - CORNER_SIZE, 0, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);
        // Bottom side
        RenderUtil.renderRepeating(this, left + CORNER_SIZE, bottom - CORNER_SIZE, internalWidth - CORNER_SIZE - 2*SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Bottom right corner
        this.blit(right - CORNER_SIZE, bottom - CORNER_SIZE, WIDTH - CORNER_SIZE, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);


        if (this.tabs.size() > 1) {
            this.minecraft.getTextureManager().bindTexture(Resources.Gui.TABS);

            int width = right - left;
            int height = bottom - top;

            for (GuiBetterAdvancementTab tab : this.tabs.values()) {
                tab.drawTab(left, top, width, height, tab == this.selectedTab);
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (GuiBetterAdvancementTab tab : this.tabs.values()) {
                tab.drawIcon(left, top, width, height, this.itemRenderer);
            }

            GlStateManager.disableBlend();
        }

        String windowTitle = I18n.format("gui.advancements");
        if (selectedTab != null) {
            windowTitle += " - " + selectedTab.getTitle();
        }
        this.font.drawString(windowTitle, left + 8, top + 6, 4210752);
    }

    private void renderToolTips(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.selectedTab != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepthTest();
            GlStateManager.translated((float) (left + PADDING), (float) (top + 2*PADDING), 400.0F);
            this.selectedTab.drawToolTips(mouseX - left - PADDING, mouseY - top - 2*PADDING, left, top, right - left - 2*PADDING, bottom - top - 3*PADDING);
            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }

        int width = right - left;
        int height = bottom - top;

        if (this.tabs.size() > 1) {
            for (GuiBetterAdvancementTab tab : this.tabs.values()) {
                if (tab.isMouseOver(left, top, width, height, mouseX, mouseY)) {
                    this.renderTooltip(tab.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    public void rootAdvancementAdded(@Nonnull Advancement advancement) {
        GuiBetterAdvancementTab guiBetterAdvancementTab = GuiBetterAdvancementTab.create(this.minecraft, this, this.tabs.size(), advancement, internalWidth - 2*SIDE, internalHeight - TOP - SIDE);

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
        BetterAdvancementEntryGui betterAdvancementEntryGui = this.getAdvancementGui(advancement);

        if (betterAdvancementEntryGui != null) {
            betterAdvancementEntryGui.getAdvancementProgress(advancementProgress);
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
    public BetterAdvancementEntryGui getAdvancementGui(Advancement advancement) {
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