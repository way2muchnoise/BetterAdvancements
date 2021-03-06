package betteradvancements.gui;

import betteradvancements.api.event.AdvancementMovedEvent;
import betteradvancements.reference.Resources;
import betteradvancements.util.RenderUtil;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
public class BetterAdvancementsScreen extends Screen implements ClientAdvancementManager.IListener {
    private static final int WIDTH = 252, HEIGHT = 140, CORNER_SIZE = 30;
    private static final int SIDE = 30, TOP = 40, BOTTOM = 30, PADDING = 9;
    private static final float MIN_ZOOM = 1, MAX_ZOOM = 2, ZOOM_STEP = 0.2F;
    private final ClientAdvancementManager clientAdvancementManager;
    private final Map<Advancement, BetterAdvancementTabGui> tabs = Maps.newLinkedHashMap();
    private BetterAdvancementTabGui selectedTab;
    private float zoom = MIN_ZOOM;
    private boolean isScrolling;
    protected int internalWidth, internalHeight;
    public static int uiScaling;
    public static boolean showDebugCoordinates = false;
    public static boolean orderTabsAlphabetically = false;
    private BetterAdvancementEntryGui advConnectedToMouse = null;

    public BetterAdvancementsScreen(ClientAdvancementManager clientAdvancementManager) {
        super(NarratorChatListener.NO_TITLE);
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
    public void onClose() {
        this.clientAdvancementManager.setListener(null);
        ClientPlayNetHandler clientPlayNetHandler = this.minecraft.getConnection();
        if (clientPlayNetHandler != null) {
            clientPlayNetHandler.send(CSeenAdvancementsPacket.closedScreen());
        }
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
        if (modifiers == 0) {
            int left = SIDE + (width - internalWidth) / 2;
            int top = TOP + (height - internalHeight) / 2;
            for (BetterAdvancementTabGui betterAdvancementTabGui : this.tabs.values()) {
                if (betterAdvancementTabGui.isMouseOver(left, top, internalWidth - 2*SIDE, internalHeight - top - BOTTOM, mouseX, mouseY)) {
                    this.clientAdvancementManager.setSelectedTab(betterAdvancementTabGui.getAdvancement(), true);
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
        if (keyCode == this.minecraft.options.keyAdvancements.getKey().getValue()) {
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double mouseDeltaX, double mouseDeltaY) {
        int left = SIDE + (width - internalWidth) / 2;
        int top = TOP + (height - internalHeight) / 2;

        if (button != 0) {
            this.isScrolling = false;
            return false;
        }

        if (!this.isScrolling) {
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
                this.advConnectedToMouse.x = (int)Math.round(this.advConnectedToMouse.x + mouseDeltaX);
                this.advConnectedToMouse.y = (int)Math.round(this.advConnectedToMouse.y + mouseDeltaY);
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

        if (this.advConnectedToMouse == null) {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.scroll(mouseDeltaX , mouseDeltaY, internalWidth - 2 * SIDE - 3 * PADDING, internalHeight - TOP - BOTTOM - 3 * PADDING);
            }
        }

        return true;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int left = SIDE + (width - internalWidth) / 2;
        int top = TOP + (height - internalHeight) / 2;

        int right = internalWidth - SIDE + (width - internalWidth) / 2;
        int bottom = internalHeight - SIDE + (height - internalHeight) / 2;

        this.renderBackground(matrixStack);
        this.renderInside(matrixStack, mouseX, mouseY, left, top, right, bottom);
        this.renderWindow(matrixStack, left, top, right, bottom);
        //Don't draw tool tips if dragging an advancement
        if (this.advConnectedToMouse == null) {
            this.renderToolTips(matrixStack, mouseX, mouseY, left, top, right, bottom);
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

        if (BetterAdvancementsScreen.showDebugCoordinates && this.selectedTab != null && mouseX < internalWidth - SIDE - PADDING && mouseX > SIDE + PADDING && mouseY < internalHeight - top + 1 && mouseY > top + PADDING * 2) {
            //If dragging an advancement, draw coordinates of advancement being moved instead of mouse coordinates
            if (this.advConnectedToMouse != null) {
                //-3 and -1 are needed to have the coordinates be rendered where the advancement starts being rendered, rather than its real position.
                int currentX = this.advConnectedToMouse.x + left + PADDING + this.selectedTab.scrollX + 3 + 1;
                int currentY = this.advConnectedToMouse.y + top + 2 * PADDING + this.selectedTab.scrollY - font.lineHeight + 1;

                font.draw(matrixStack, this.advConnectedToMouse.x + "," + this.advConnectedToMouse.y, currentX, currentY, 0x000000);
            } else {
                //Draws a string containing the current position above the mouse. Locked to inside the advancement window.
                int xMouse = mouseX - left - PADDING;
                int yMouse = mouseY - top - 2 * PADDING;
                //-3 and -1 are needed to have the position be where the advancement starts being rendered, rather than its real position.
                int currentX = xMouse - this.selectedTab.scrollX - 3 - 1;
                int currentY = yMouse - this.selectedTab.scrollY - 1;

                font.draw(matrixStack, currentX + "," + currentY, mouseX, mouseY - font.lineHeight, 0x000000);
            }
        }
    }

    private void renderInside(MatrixStack matrixStack, int mouseX, int mouseY, int left, int top, int right, int bottom) {
        BetterAdvancementTabGui betterAdvancementTabGui = this.selectedTab;
        int boxLeft = left + PADDING;
        int boxTop = top + 2*PADDING;
        int boxRight = right - PADDING;
        int boxBottom = bottom - PADDING;

        int width = boxRight - boxLeft;
        int height = boxBottom - boxTop;

        if (betterAdvancementTabGui == null) {
            fill(matrixStack, boxLeft, boxTop, boxRight, boxBottom, -16777216);
            String s = I18n.get("advancements.empty");
            int i = this.font.width(s);
            this.font.draw(matrixStack, s, boxLeft + (width - i) / 2, boxTop + height / 2 - this.font.lineHeight, -1);
            this.font.draw(matrixStack, ":(", boxLeft + (width - this.font.width(":(")) / 2, boxTop + height / 2 + this.font.lineHeight, -1);
        } else {
            matrixStack.pushPose();
            matrixStack.translate(boxLeft, boxTop, 0.0F);
            RenderSystem.enableDepthTest();
            betterAdvancementTabGui.drawContents(matrixStack, width, height);
            matrixStack.popPose();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        }
    }

    public void renderWindow(MatrixStack matrixStack, int left, int top, int right, int bottom) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderHelper.turnOff();
        this.minecraft.getTextureManager().bind(Resources.Gui.WINDOW);
        // Top left corner
        this.blit(matrixStack, left, top, 0, 0, CORNER_SIZE, CORNER_SIZE);
        // Top side
        RenderUtil.renderRepeating(this, matrixStack, left + CORNER_SIZE, top, internalWidth - CORNER_SIZE - 2*SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, 0, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Top right corner
        this.blit(matrixStack, right - CORNER_SIZE, top, WIDTH - CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE);
        // Left side
        RenderUtil.renderRepeating(this, matrixStack, left, top + CORNER_SIZE, CORNER_SIZE, bottom - top - 2 * CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Right side
        RenderUtil.renderRepeating(this, matrixStack, right - CORNER_SIZE, top + CORNER_SIZE, CORNER_SIZE, bottom - top - 2 * CORNER_SIZE, WIDTH - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Bottom left corner
        this.blit(matrixStack, left, bottom - CORNER_SIZE, 0, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);
        // Bottom side
        RenderUtil.renderRepeating(this, matrixStack, left + CORNER_SIZE, bottom - CORNER_SIZE, internalWidth - CORNER_SIZE - 2*SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Bottom right corner
        this.blit(matrixStack, right - CORNER_SIZE, bottom - CORNER_SIZE, WIDTH - CORNER_SIZE, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);


        if (this.tabs.size() > 1) {
            this.minecraft.getTextureManager().bind(Resources.Gui.TABS);

            int width = right - left;
            int height = bottom - top;

            for (BetterAdvancementTabGui tab : this.tabs.values()) {
                tab.drawTab(matrixStack, left, top, width, height, tab == this.selectedTab);
            }

            RenderSystem.enableRescaleNormal();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.turnBackOn();

            for (BetterAdvancementTabGui tab : this.tabs.values()) {
                tab.drawIcon(matrixStack, left, top, width, height, this.itemRenderer);
            }

            RenderSystem.disableBlend();
        }

        String windowTitle = I18n.get("gui.advancements");
        if (selectedTab != null) {
            windowTitle += " - " + selectedTab.getTitle().getString();
        }
        this.font.draw(matrixStack, windowTitle, left + 8, top + 6, 4210752);
    }

    private void renderToolTips(MatrixStack matrixStack, int mouseX, int mouseY, int left, int top, int right, int bottom) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.selectedTab != null) {
            matrixStack.pushPose();
            RenderSystem.enableDepthTest();
            matrixStack.translate(left + PADDING, top + 2*PADDING, 400.0F);
            this.selectedTab.drawToolTips(matrixStack,mouseX - left - PADDING, mouseY - top - 2*PADDING, left, top, right - left - 2*PADDING, bottom - top - 3*PADDING);
            RenderSystem.disableDepthTest();
            matrixStack.popPose();
        }

        int width = right - left;
        int height = bottom - top;

        if (this.tabs.size() > 1) {
            for (BetterAdvancementTabGui tab : this.tabs.values()) {
                if (tab.isMouseOver(left, top, width, height, mouseX, mouseY)) {
                    this.renderTooltip(matrixStack, tab.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void onAddAdvancementRoot(@Nonnull Advancement advancement) {
        BetterAdvancementTabGui betterAdvancementTabGui = BetterAdvancementTabGui.create(this.minecraft, this, this.tabs.size(), advancement, internalWidth - 2*SIDE, internalHeight - TOP - SIDE);

        if (betterAdvancementTabGui != null) {
            this.tabs.put(advancement, betterAdvancementTabGui);
        }
    }

    @Override
    public void onRemoveAdvancementRoot(@Nonnull Advancement advancement) {
    }

    @Override
    public void onAddAdvancementTask(@Nonnull Advancement advancement) {
        BetterAdvancementTabGui betterAdvancementTabGui = this.getTab(advancement);

        if (betterAdvancementTabGui != null) {
            betterAdvancementTabGui.addAdvancement(advancement);
        }
    }

    @Override
    public void onRemoveAdvancementTask(@Nonnull Advancement advancement) {
    }

    @Override
    public void onUpdateAdvancementProgress(@Nonnull Advancement advancement, @Nonnull AdvancementProgress advancementProgress) {
        BetterAdvancementEntryGui betterAdvancementEntryGui = this.getAdvancementGui(advancement);

        if (betterAdvancementEntryGui != null) {
            betterAdvancementEntryGui.getAdvancementProgress(advancementProgress);
        }
    }

    @Override
    public void onSelectedTabChanged(@Nullable Advancement advancement) {
        this.selectedTab = this.tabs.get(advancement);
    }

    @Override
    public void onAdvancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public BetterAdvancementEntryGui getAdvancementGui(Advancement advancement) {
        BetterAdvancementTabGui betterAdvancementTabGui = this.getTab(advancement);
        return betterAdvancementTabGui == null ? null : betterAdvancementTabGui.getAdvancementGui(advancement);
    }

    @Nullable
    private BetterAdvancementTabGui getTab(@Nonnull Advancement advancement) {
        while (advancement.getParent() != null) {
            advancement = advancement.getParent();
        }

        return this.tabs.get(advancement);
    }
}