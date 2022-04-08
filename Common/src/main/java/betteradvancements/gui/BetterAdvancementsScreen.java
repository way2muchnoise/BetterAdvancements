package betteradvancements.gui;

import betteradvancements.platform.Services;
import betteradvancements.reference.Resources;
import betteradvancements.util.RenderUtil;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BetterAdvancementsScreen extends Screen implements ClientAdvancements.Listener {
    private static final Component VERY_SAD_LABEL = new TranslatableComponent("advancements.sad_label");
    private static final Component NO_ADVANCEMENTS_LABEL = new TranslatableComponent("advancements.empty");
    private static final Component TITLE = new TranslatableComponent("gui.advancements");
    private static final int WIDTH = 252, HEIGHT = 140, CORNER_SIZE = 30;
    private static final int SIDE = 30, TOP = 40, BOTTOM = 30, PADDING = 9;
    private static final float MIN_ZOOM = 1, MAX_ZOOM = 2, ZOOM_STEP = 0.2F;
    private final ClientAdvancements clientAdvancements;
    private final Map<Advancement, BetterAdvancementTab> tabs = Maps.newLinkedHashMap();
    private BetterAdvancementTab selectedTab;
    private float zoom = MIN_ZOOM;
    private boolean isScrolling;
    protected int internalWidth, internalHeight;
    public static int uiScaling;
    public static boolean showDebugCoordinates = false;
    public static boolean orderTabsAlphabetically = false;
    private BetterAdvancementWidget advConnectedToMouse = null;

    public BetterAdvancementsScreen(ClientAdvancements clientAdvancements) {
        super(NarratorChatListener.NO_TITLE);
        this.clientAdvancements = clientAdvancements;
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
        this.clientAdvancements.setListener(this);

        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.clientAdvancements.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
        } else {
            this.clientAdvancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onClose() {
        this.clientAdvancements.setListener(null);
        ClientPacketListener clientpacketlistener = this.minecraft.getConnection();
        if (clientpacketlistener != null) {
            clientpacketlistener.send(ServerboundSeenAdvancementsPacket.closedScreen());
        }
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
        if (modifiers == 0) {
            int left = SIDE + (width - internalWidth) / 2;
            int top = TOP + (height - internalHeight) / 2;
            for (BetterAdvancementTab betterAdvancementTabGui : this.tabs.values()) {
                if (betterAdvancementTabGui.isMouseOver(left, top, internalWidth - 2*SIDE, internalHeight - top - BOTTOM, mouseX, mouseY)) {
                    this.clientAdvancements.setSelectedTab(betterAdvancementTabGui.getAdvancement(), true);
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
        if (this.minecraft.options.keyAdvancements.matches(keyCode, scanCode)) {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
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
                    for (BetterAdvancementWidget betterAdvancementEntryScreen : this.selectedTab.guis.values()) {
                        if (betterAdvancementEntryScreen.isMouseOver(this.selectedTab.scrollX, this.selectedTab.scrollY, mouseX - left - PADDING, mouseY - top - 2*PADDING)) {

                            if (betterAdvancementEntryScreen.betterDisplayInfo.allowDragging())
                            {
                                this.advConnectedToMouse = betterAdvancementEntryScreen;
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
                Services.PLATFORM.getEventHelper().postAdvancementMovementEvent(advConnectedToMouse);
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int left = SIDE + (width - internalWidth) / 2;
        int top = TOP + (height - internalHeight) / 2;

        int right = internalWidth - SIDE + (width - internalWidth) / 2;
        int bottom = internalHeight - SIDE + (height - internalHeight) / 2;

        this.renderBackground(poseStack);
        this.renderInside(poseStack, mouseX, mouseY, left, top, right, bottom);
        this.renderWindow(poseStack, left, top, right, bottom);
        //Don't draw tool tips if dragging an advancement
        if (this.advConnectedToMouse == null) {
            this.renderToolTips(poseStack, mouseX, mouseY, left, top, right, bottom);
        }
        
        //Draw guide lines to all advancements at 45 or 90 degree angles.
        if (this.advConnectedToMouse != null)
        {
            for (BetterAdvancementWidget betterAdvancementEntryScreen : this.selectedTab.guis.values()) {
                if (betterAdvancementEntryScreen != this.advConnectedToMouse)
                {
                    int x1 = betterAdvancementEntryScreen.x + left + PADDING + this.selectedTab.scrollX + 3;
                    int x2 = this.advConnectedToMouse.x + left + PADDING + this.selectedTab.scrollX + 3;
                    int y1 = betterAdvancementEntryScreen.y + top + 2 * PADDING + this.selectedTab.scrollY;
                    int y2 = this.advConnectedToMouse.y + top + 2 * PADDING + this.selectedTab.scrollY;
                    int centerX1 = betterAdvancementEntryScreen.x + left + PADDING + this.selectedTab.scrollX + 3 + BetterAdvancementWidget.ADVANCEMENT_SIZE / 2;
                    int centerX2 = this.advConnectedToMouse.x + left + PADDING + this.selectedTab.scrollX + 3 + BetterAdvancementWidget.ADVANCEMENT_SIZE / 2;
                    int centerY1 = betterAdvancementEntryScreen.y + top + 2 * PADDING + this.selectedTab.scrollY + BetterAdvancementWidget.ADVANCEMENT_SIZE / 2;
                    int centerY2 = this.advConnectedToMouse.y + top + 2 * PADDING + this.selectedTab.scrollY + BetterAdvancementWidget.ADVANCEMENT_SIZE / 2;
                    double degrees = Math.toDegrees(Math.atan2(centerX1 - centerX2, centerY1 - centerY2));
                    if (degrees < 0)
                    {
                        degrees += 360;
                    }
                    
                    if (betterAdvancementEntryScreen.x == this.advConnectedToMouse.x)
                    {
                        if (y1 > y2)
                        {
                            //Draw right
                            RenderUtil.drawRect(x1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2, y2, 1, 0x00FF00);
                            //Draw bottom for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x2, y1, 1, 0x00FF00);
                            //Draw bottom for top
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for top
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2, x2, y2, 1, 0x00FF00);
                            //Draw left
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                        }
                        else
                        {
                            //Draw right
                            RenderUtil.drawRect(x1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2, y1, 1, 0x00FF00);
                            //Draw bottom for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for bottom
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2, x2, y2, 1, 0x00FF00);
                            //Draw bottom for top
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw top for top
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x2, y1, 1, 0x00FF00);
                            //Draw left
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, 1, 0x00FF00);
                        }
                    }
                    if (betterAdvancementEntryScreen.y == this.advConnectedToMouse.y)
                    {
                        if (x1 > x2)
                        {
                            //Draw top
                            RenderUtil.drawRect(x2, y1, x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                            //Draw left for right
                            RenderUtil.drawRect(x1, y1, x1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for right
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw left for left
                            RenderUtil.drawRect(x2, y1, x2, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for left
                            RenderUtil.drawRect(x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw bottom
                            RenderUtil.drawRect(x2, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        }
                        else
                        {
                            //Draw left
                            RenderUtil.drawRect(x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x1, y2, 1, 0x00FF00);
                            //Draw left for right
                            RenderUtil.drawRect(x2, y1, x2, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for right
                            RenderUtil.drawRect(x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw left for left
                            RenderUtil.drawRect(x1, y1, x1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right for left
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            //Draw right
                            RenderUtil.drawRect(x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        }
                    }
                    if (degrees == 45 || degrees == 135 || degrees == 225 || degrees == 315)
                    {
                        //Draw lines around each advancement
                        //First
                        //Top
                        RenderUtil.drawRect(x1, y1, x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, 1, 0x00FF00);
                        //Bottom
                        RenderUtil.drawRect(x1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Left
                        RenderUtil.drawRect(x1, y1, x1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Right
                        RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Second
                        //Top
                        RenderUtil.drawRect(x2, y2, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                        //Bottom
                        RenderUtil.drawRect(x2, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Left
                        RenderUtil.drawRect(x2, y2, x2, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        //Right
                        RenderUtil.drawRect(x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                        
                        if (degrees == 45 || degrees == 225)
                        {
                            RenderUtil.drawRect(x1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2, 1, 0x00FF00);
                        }
                        else if (degrees == 135 || degrees == 315)
                        {
                            RenderUtil.drawRect(x1, y1, x2, y2, 1, 0x00FF00);
                            RenderUtil.drawRect(x1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y1 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, x2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, y2 + BetterAdvancementWidget.ADVANCEMENT_SIZE - 1, 1, 0x00FF00);
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

                font.draw(poseStack, this.advConnectedToMouse.x + "," + this.advConnectedToMouse.y, currentX, currentY, 0x000000);
            } else {
                //Draws a string containing the current position above the mouse. Locked to inside the advancement window.
                int xMouse = mouseX - left - PADDING;
                int yMouse = mouseY - top - 2 * PADDING;
                //-3 and -1 are needed to have the position be where the advancement starts being rendered, rather than its real position.
                int currentX = xMouse - this.selectedTab.scrollX - 3 - 1;
                int currentY = yMouse - this.selectedTab.scrollY - 1;

                font.draw(poseStack, currentX + "," + currentY, mouseX, mouseY - font.lineHeight, 0x000000);
            }
        }
    }

    private void renderInside(PoseStack poseStack, int mouseX, int mouseY, int left, int top, int right, int bottom) {
        BetterAdvancementTab betterAdvancementTab = this.selectedTab;
        int boxLeft = left + PADDING;
        int boxTop = top + 2*PADDING;
        int boxRight = right - PADDING;
        int boxBottom = bottom - PADDING;

        int width = boxRight - boxLeft;
        int height = boxBottom - boxTop;

        if (betterAdvancementTab == null) {
            fill(poseStack, boxLeft, boxTop, boxRight, boxBottom, -16777216);
            this.font.draw(poseStack, NO_ADVANCEMENTS_LABEL, boxLeft + (width - this.font.width(NO_ADVANCEMENTS_LABEL)) / 2, boxTop + height / 2 - this.font.lineHeight, -1);
            this.font.draw(poseStack, VERY_SAD_LABEL, boxLeft + (width - this.font.width(VERY_SAD_LABEL)) / 2, boxTop + height / 2 + this.font.lineHeight, -1);
        } else {
            PoseStack viewMatrixPoseStack = RenderSystem.getModelViewStack();
            viewMatrixPoseStack.pushPose();
            viewMatrixPoseStack.translate(boxLeft, boxTop, 0.0F);
            RenderSystem.applyModelViewMatrix();
            betterAdvancementTab.drawContents(poseStack, width, height);
            viewMatrixPoseStack.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        }
    }

    public void renderWindow(PoseStack poseStack, int left, int top, int right, int bottom) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Resources.Gui.WINDOW);
        // Top left corner
        this.blit(poseStack, left, top, 0, 0, CORNER_SIZE, CORNER_SIZE);
        // Top side
        RenderUtil.renderRepeating(this, poseStack, left + CORNER_SIZE, top, internalWidth - CORNER_SIZE - 2*SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, 0, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Top right corner
        this.blit(poseStack, right - CORNER_SIZE, top, WIDTH - CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE);
        // Left side
        RenderUtil.renderRepeating(this, poseStack, left, top + CORNER_SIZE, CORNER_SIZE, bottom - top - 2 * CORNER_SIZE, 0, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Right side
        RenderUtil.renderRepeating(this, poseStack, right - CORNER_SIZE, top + CORNER_SIZE, CORNER_SIZE, bottom - top - 2 * CORNER_SIZE, WIDTH - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE - CORNER_SIZE);
        // Bottom left corner
        this.blit(poseStack, left, bottom - CORNER_SIZE, 0, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);
        // Bottom side
        RenderUtil.renderRepeating(this, poseStack, left + CORNER_SIZE, bottom - CORNER_SIZE, internalWidth - CORNER_SIZE - 2*SIDE - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE, HEIGHT - CORNER_SIZE, WIDTH - CORNER_SIZE - CORNER_SIZE, CORNER_SIZE);
        // Bottom right corner
        this.blit(poseStack, right - CORNER_SIZE, bottom - CORNER_SIZE, WIDTH - CORNER_SIZE, HEIGHT - CORNER_SIZE, CORNER_SIZE, CORNER_SIZE);



        if (this.tabs.size() > 1) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, Resources.Gui.TABS);

            int width = right - left;
            int height = bottom - top;

            for (BetterAdvancementTab tab : this.tabs.values()) {
                tab.drawTab(poseStack, left, top, width, height, tab == this.selectedTab);
            }

            RenderSystem.defaultBlendFunc();

            for (BetterAdvancementTab tab : this.tabs.values()) {
                tab.drawIcon(poseStack, left, top, width, height, this.itemRenderer);
            }

            RenderSystem.disableBlend();
        }

        FormattedCharSequence windowTitle = TITLE.getVisualOrderText();
        if (selectedTab != null) {
            windowTitle = FormattedCharSequence.composite(
                windowTitle,
                new TextComponent(" - ").getVisualOrderText(),
                selectedTab.getTitle().getVisualOrderText()
            );
        }
        this.font.draw(poseStack, windowTitle, left + 8, top + 6, 4210752);
    }

    private void renderToolTips(PoseStack poseStack, int mouseX, int mouseY, int left, int top, int right, int bottom) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.selectedTab != null) {
            PoseStack viewMatrixPoseStack = RenderSystem.getModelViewStack();
            viewMatrixPoseStack.pushPose();
            viewMatrixPoseStack.translate(left + PADDING, top + 2*PADDING, 400.0D);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableDepthTest();
            this.selectedTab.drawToolTips(poseStack,mouseX - left - PADDING, mouseY - top - 2*PADDING, left, top, right - left - 2*PADDING, bottom - top - 3*PADDING);
            RenderSystem.disableDepthTest();
            viewMatrixPoseStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        int width = right - left;
        int height = bottom - top;

        if (this.tabs.size() > 1) {
            for (BetterAdvancementTab tab : this.tabs.values()) {
                if (tab.isMouseOver(left, top, width, height, mouseX, mouseY)) {
                    this.renderTooltip(poseStack, tab.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void onAddAdvancementRoot(@Nonnull Advancement advancement) {
        BetterAdvancementTab betterAdvancementTabGui = BetterAdvancementTab.create(this.minecraft, this, this.tabs.size(), advancement, internalWidth - 2*SIDE, internalHeight - TOP - SIDE);

        if (betterAdvancementTabGui != null) {
            this.tabs.put(advancement, betterAdvancementTabGui);
        }
    }

    @Override
    public void onRemoveAdvancementRoot(@Nonnull Advancement advancement) {
    }

    @Override
    public void onAddAdvancementTask(@Nonnull Advancement advancement) {
        BetterAdvancementTab betterAdvancementTabGui = this.getTab(advancement);

        if (betterAdvancementTabGui != null) {
            betterAdvancementTabGui.addAdvancement(advancement);
        }
    }

    @Override
    public void onRemoveAdvancementTask(@Nonnull Advancement advancement) {
    }

    @Override
    public void onUpdateAdvancementProgress(@Nonnull Advancement advancement, @Nonnull AdvancementProgress advancementProgress) {
        BetterAdvancementWidget betterAdvancementEntryScreen = this.getAdvancementGui(advancement);

        if (betterAdvancementEntryScreen != null) {
            betterAdvancementEntryScreen.getAdvancementProgress(advancementProgress);
        }
    }

    @Override
    public void onSelectedTabChanged(@Nullable Advancement advancement) {
        if (this.selectedTab != null) {
            this.selectedTab.storeScroll();
        }
        this.selectedTab = this.tabs.get(advancement);
        if (this.selectedTab != null) {
            this.selectedTab.loadScroll();
        }
    }

    @Override
    public void onAdvancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public BetterAdvancementWidget getAdvancementGui(Advancement advancement) {
        BetterAdvancementTab betterAdvancementTab = this.getTab(advancement);
        return betterAdvancementTab == null ? null : betterAdvancementTab.getWidget(advancement);
    }

    @Nullable
    private BetterAdvancementTab getTab(@Nonnull Advancement advancement) {
        while (advancement.getParent() != null) {
            advancement = advancement.getParent();
        }

        return this.tabs.get(advancement);
    }
}