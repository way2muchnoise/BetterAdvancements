package betteradvancements.gui;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.api.IBetterAdvancementEntryGui;
import betteradvancements.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.platform.Services;
import betteradvancements.reference.Resources;
import betteradvancements.util.CriterionGrid;
import betteradvancements.util.RenderUtil;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BetterAdvancementWidget extends GuiComponent implements IBetterAdvancementEntryGui {
    protected static final int ADVANCEMENT_SIZE = 26;
    private static final int CORNER_SIZE = 10;
    private static final int WIDGET_WIDTH = 256, WIDGET_HEIGHT = 26, TITLE_SIZE = 32, ICON_OFFSET = 128, ICON_SIZE = 26;

    private final BetterAdvancementTab betterAdvancementTabGui;
    private final Advancement advancement;
    protected final BetterDisplayInfo betterDisplayInfo;
    private final DisplayInfo displayInfo;
    private final String title;
    private int width;
    private List<FormattedCharSequence> description;
    private CriterionGrid criterionGrid;
    private final Minecraft minecraft;
    private BetterAdvancementWidget parent;
    private final List<BetterAdvancementWidget> children = Lists.newArrayList();
    private AdvancementProgress advancementProgress;
    protected int x, y;
    private final int screenScale;

    public BetterAdvancementWidget(BetterAdvancementTab betterAdvancementTabGui, Minecraft mc, Advancement advancement, DisplayInfo displayInfo) {
        this.betterAdvancementTabGui = betterAdvancementTabGui;
        this.advancement = advancement;
        this.betterDisplayInfo = betterAdvancementTabGui.getBetterDisplayInfo(advancement);
        this.displayInfo = displayInfo;
        this.minecraft = mc;
        this.title = displayInfo.getTitle().getString(163);
        this.x = this.betterDisplayInfo.getPosX() != null ? this.betterDisplayInfo.getPosX() : Mth.floor(displayInfo.getX() * 32.0F);
        this.y = this.betterDisplayInfo.getPosY() != null ? this.betterDisplayInfo.getPosY() : Mth.floor(displayInfo.getY() * 27.0F);
        this.refreshHover();
        this.screenScale = mc.getWindow().calculateScale(0, false);
    }

    private void refreshHover() {
        Minecraft mc = this.minecraft;
        int k = 0;
        if (advancement.getMaxCriteraRequired() > 1) {
            // Add some space for the requirement counter
            int strLengthRequirementCount = String.valueOf(advancement.getMaxCriteraRequired()).length();
            k = mc.font.width("  ") + mc.font.width("0") * strLengthRequirementCount * 2 + mc.font.width("/");
        }
        int titleWidth = 29 + mc.font.width(this.title) + k;
        BetterAdvancementsScreen screen = betterAdvancementTabGui.getScreen();
        this.criterionGrid = CriterionGrid.findOptimalCriterionGrid(advancement, advancementProgress, screen.width / 2, mc.font);
        int maxWidth;
        
        if (!CriterionGrid.requiresShift || Screen.hasShiftDown()) {
            maxWidth = Math.max(titleWidth, this.criterionGrid.width);
        }
        else {
            maxWidth =  titleWidth;
        }
        this.description = this.findOptimalLines(displayInfo.getDescription(), maxWidth);

        for (FormattedCharSequence line : this.description) {
            maxWidth = Math.max(maxWidth, mc.font.width(line));
        }

        this.width = maxWidth + 8;
    }

    private List<FormattedCharSequence> findOptimalLines(Component line, int width) {
        if (line.getString().isEmpty()) {
            return Collections.emptyList();
        } else {
            List<FormattedCharSequence> list = this.minecraft.font.split(line, width);
            if (list.size() > 1) {
                width = Math.max(width, betterAdvancementTabGui.getScreen().internalWidth / 4);
                list = this.minecraft.font.split(line, width);
            }
            while (list.size() > 5 && width < WIDGET_WIDTH * 1.5 && width < betterAdvancementTabGui.getScreen().internalWidth / 2.5) {
                width += width / 4;
                list = this.minecraft.font.split(line, width);
            }
            return list;
        }
    }

    @Nullable
    private BetterAdvancementWidget getFirstVisibleParent(Advancement advancementIn) {
        while (true) {
            advancementIn = advancementIn.getParent();

            if (advancementIn == null || advancementIn.getDisplay() != null) {
                break;
            }
        }

        if (advancementIn != null && advancementIn.getDisplay() != null) {
            return this.betterAdvancementTabGui.getWidget(advancementIn);
        } else {
            return null;
        }
    }

    public void drawConnectivity(PoseStack poseStack, int scrollX, int scrollY, boolean drawInside) {
        //Check if connections should be drawn at all
        if (!this.betterDisplayInfo.hideLines()) {
            //Draw connection to parent
            if (this.parent != null) {
                
                this.drawConnection(poseStack, this.parent, scrollX, scrollY, drawInside);
            }
            
            //Create and post event to get extra connections
            IAdvancementDrawConnectionsEvent event = Services.PLATFORM.getEventHelper().postAdvancementDrawConnectionsEvent(this.advancement);

            //Draw extra connections from event
            for (Advancement parent : event.getExtraConnections()) {
                final BetterAdvancementWidget parentGui = this.betterAdvancementTabGui.getWidget(parent);
                
                if (parentGui != null) {
                    this.drawConnection(poseStack, parentGui, scrollX, scrollY, drawInside);
                }
            }
        }
        //Draw child connections
        for (BetterAdvancementWidget betterAdvancementWidget : this.children) {
            betterAdvancementWidget.drawConnectivity(poseStack, scrollX, scrollY, drawInside);
        }
    }
    
    /**
     * Draws connection line between this advancement and the advancement supplied in parent.
     */
    public void drawConnection(PoseStack poseStack, BetterAdvancementWidget parent, int scrollX, int scrollY, boolean drawInside) {
        int innerLineColor = this.advancementProgress != null && this.advancementProgress.isDone() ? betterDisplayInfo.getCompletedLineColor() : betterDisplayInfo.getUnCompletedLineColor();
        int borderLineColor = 0xFF000000;
        
        if (this.betterDisplayInfo.drawDirectLines()) {
            double x1 = scrollX + this.x + ADVANCEMENT_SIZE / 2 + 3;
            double y1 = scrollY + this.y + ADVANCEMENT_SIZE / 2;
            double x2 = scrollX + parent.x + ADVANCEMENT_SIZE / 2 + 3;
            double y2 = scrollY + parent.y + ADVANCEMENT_SIZE / 2;
            
            double width;
            boolean perpendicular = x1 == x2 || y1 == y2;
            
            if (!perpendicular) {
                switch (this.screenScale) {
                    case 1 -> width = drawInside ? 1.5 : 0.5;
                    case 2 -> width = drawInside ? 2.25 : 0.75;
                    case 3 -> width = drawInside ? 2 : 0.6666666666666667;
                    case 4 -> width = drawInside ? 2.125 : 0.625;
                    default -> width = drawInside ? 3 : 1;
                }
                if (drawInside) {
                    RenderUtil.drawRect(x1 - .75, y1 - .75, x2 - .75, y2 - .75, width, borderLineColor);
                }
                else {
                    RenderUtil.drawRect(x1, y1, x2, y2, width, innerLineColor);
                }
            }
            else {
                width = drawInside ? 3 : 1;
                
                if (drawInside) {
                    RenderUtil.drawRect(x1 - 1, y1 - 1, x2 - 1, y2 - 1, width, borderLineColor);
                }
                else {
                    RenderUtil.drawRect(x1, y1, x2, y2, width, innerLineColor);
                }
            }
        }
        else {
            int startX = scrollX + parent.x + ADVANCEMENT_SIZE / 2;
            int endXHalf = scrollX + parent.x + ADVANCEMENT_SIZE + 6; // 6 = 32 - 26
            int startY = scrollY + parent.y + ADVANCEMENT_SIZE / 2;
            int endX = scrollX + this.x + ADVANCEMENT_SIZE / 2;
            int endY = scrollY + this.y + ADVANCEMENT_SIZE / 2;
            
            if (drawInside) {
                this.hLine(poseStack, endXHalf, startX, startY - 1, borderLineColor);
                this.hLine(poseStack, endXHalf + 1, startX, startY, borderLineColor);
                this.hLine(poseStack, endXHalf, startX, startY + 1, borderLineColor);
                this.hLine(poseStack, endX, endXHalf - 1, endY - 1, borderLineColor);
                this.hLine(poseStack, endX, endXHalf - 1, endY, borderLineColor);
                this.hLine(poseStack, endX, endXHalf - 1, endY + 1, borderLineColor);
                this.vLine(poseStack, endXHalf - 1, endY, startY, borderLineColor);
                this.vLine(poseStack, endXHalf + 1, endY, startY, borderLineColor);
            } else {
                this.hLine(poseStack, endXHalf, startX, startY, innerLineColor);
                this.hLine(poseStack, endX, endXHalf, endY, innerLineColor);
                this.vLine(poseStack, endXHalf, endY, startY, innerLineColor);
            }
        }
    }

    public void draw(PoseStack poseStack, int scrollX, int scrollY) {
        if (!this.displayInfo.isHidden() || this.advancementProgress != null && this.advancementProgress.isDone()) {
            float f = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
            AdvancementWidgetType advancementState;

            if (f >= 1.0F) {
                advancementState = AdvancementWidgetType.OBTAINED;
            } else {
                advancementState = AdvancementWidgetType.UNOBTAINED;
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, Resources.Gui.WIDGETS);
            RenderUtil.setColor(betterDisplayInfo.getIconColor(advancementState));
            RenderSystem.enableBlend();
            this.blit(poseStack, scrollX + this.x + 3, scrollY + this.y, this.displayInfo.getFrame().getTexture(), ICON_OFFSET + ICON_SIZE * betterDisplayInfo.getIconYMultiplier(advancementState), ICON_SIZE, ICON_SIZE);
            this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.displayInfo.getIcon(), scrollX + this.x + 8, scrollY + this.y + 5);
        }

        for (BetterAdvancementWidget betterAdvancementWidget : this.children) {
            betterAdvancementWidget.draw(poseStack, scrollX, scrollY);
        }
    }

    public void getAdvancementProgress(AdvancementProgress advancementProgressIn) {
        this.advancementProgress = advancementProgressIn;
        this.refreshHover();
    }

    public void addGuiAdvancement(BetterAdvancementWidget betterAdvancementEntryScreen) {
        this.children.add(betterAdvancementEntryScreen);
    }

    public void drawHover(PoseStack poseStack, int scrollX, int scrollY, float fade, int left, int top) {
        this.refreshHover();
        boolean drawLeft = left + scrollX + this.x + this.width + ADVANCEMENT_SIZE >= this.betterAdvancementTabGui.getScreen().internalWidth;
        String s = this.advancementProgress == null ? null : this.advancementProgress.getProgressText();
        int i = s == null ? 0 : this.minecraft.font.width(s);
        boolean drawTop;
        
        if (!CriterionGrid.requiresShift || Screen.hasShiftDown()) {
            if (this.criterionGrid.height < this.betterAdvancementTabGui.getScreen().height) {
                drawTop = top + scrollY + this.y + this.description.size() * this.minecraft.font.lineHeight + this.criterionGrid.height + 50 >= this.betterAdvancementTabGui.getScreen().height;
            } else {
                // Always draw on the bottom if the grid is larger than the screen
                drawTop = false;
            }
        }
        else {
            drawTop = top + scrollY + this.y + this.description.size() * this.minecraft.font.lineHeight + 50 >= this.betterAdvancementTabGui.getScreen().height;
        }

        float percentageObtained = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
        int j = Mth.floor(percentageObtained * (float) this.width);
        AdvancementWidgetType stateTitleLeft;
        AdvancementWidgetType stateTitleRight;
        AdvancementWidgetType stateIcon;

        if (percentageObtained >= 1.0F) {
            j = this.width / 2;
            stateTitleLeft = AdvancementWidgetType.OBTAINED;
            stateTitleRight = AdvancementWidgetType.OBTAINED;
            stateIcon = AdvancementWidgetType.OBTAINED;
        } else if (j < 2) {
            j = this.width / 2;
            stateTitleLeft = AdvancementWidgetType.UNOBTAINED;
            stateTitleRight = AdvancementWidgetType.UNOBTAINED;
            stateIcon = AdvancementWidgetType.UNOBTAINED;
        } else if (j > this.width - 2) {
            j = this.width / 2;
            stateTitleLeft = AdvancementWidgetType.OBTAINED;
            stateTitleRight = AdvancementWidgetType.OBTAINED;
            stateIcon = AdvancementWidgetType.UNOBTAINED;
        } else {
            stateTitleLeft = AdvancementWidgetType.OBTAINED;
            stateTitleRight = AdvancementWidgetType.UNOBTAINED;
            stateIcon = AdvancementWidgetType.UNOBTAINED;
        }

        int k = this.width - j;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Resources.Gui.WIDGETS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        int drawY = scrollY + this.y;
        int drawX;

        if (drawLeft) {
            drawX = scrollX + this.x - this.width + ADVANCEMENT_SIZE + 6;
        } else {
            drawX = scrollX + this.x;
        }
        int boxHeight;
        
        if (!CriterionGrid.requiresShift || Screen.hasShiftDown()) {
            boxHeight = TITLE_SIZE + this.description.size() * this.minecraft.font.lineHeight + this.criterionGrid.height;
        }
        else {
            boxHeight = TITLE_SIZE + this.description.size() * this.minecraft.font.lineHeight;
        }

        if (!this.description.isEmpty()) {
            if (drawTop) {
                this.render9Sprite(poseStack, drawX, drawY + ADVANCEMENT_SIZE - boxHeight, this.width, boxHeight, CORNER_SIZE, WIDGET_WIDTH, WIDGET_HEIGHT, 0, 52);
            } else {
                this.render9Sprite(poseStack, drawX, drawY, this.width, boxHeight, CORNER_SIZE, WIDGET_WIDTH, WIDGET_HEIGHT, 0, 52);
            }
        }

        // Title left side
        RenderUtil.setColor(betterDisplayInfo.getTitleColor(stateTitleLeft));
        int left_side = Math.min(j, WIDGET_WIDTH - 16);
        this.blit(poseStack, drawX, drawY, 0, betterDisplayInfo.getTitleYMultiplier(stateTitleLeft) * WIDGET_HEIGHT, left_side, WIDGET_HEIGHT);
        if (left_side < j) {
            this.blit(poseStack, drawX + left_side, drawY, 16, betterDisplayInfo.getTitleYMultiplier(stateTitleLeft) * WIDGET_HEIGHT, j - left_side, WIDGET_HEIGHT);
        }
        // Title right side
        RenderUtil.setColor(betterDisplayInfo.getTitleColor(stateTitleRight));
        int right_side = Math.min(k, WIDGET_WIDTH - 16);
        this.blit(poseStack, drawX + j, drawY, WIDGET_WIDTH - right_side, betterDisplayInfo.getTitleYMultiplier(stateTitleRight) * WIDGET_HEIGHT, right_side, WIDGET_HEIGHT);
        if (right_side < k) {
            // + and - 2 is to create some overlap in the drawing when it extends past the max length of the texture
            this.blit(poseStack, drawX + j + right_side - 2, drawY, WIDGET_WIDTH - k + right_side - 2, betterDisplayInfo.getTitleYMultiplier(stateTitleRight) * WIDGET_HEIGHT, k - right_side + 2, WIDGET_HEIGHT);
        }
        // Advancement icon
        RenderUtil.setColor(betterDisplayInfo.getIconColor(stateIcon));
        this.blit(poseStack, scrollX + this.x + 3, scrollY + this.y, this.displayInfo.getFrame().getTexture(), ICON_OFFSET + ICON_SIZE * betterDisplayInfo.getIconYMultiplier(stateIcon), ICON_SIZE, ICON_SIZE);

        if (drawLeft) {
            this.minecraft.font.drawShadow(poseStack, this.title, (float) (drawX + 5), (float) (scrollY + this.y + 9), -1);

            if (s != null) {
                this.minecraft.font.drawShadow(poseStack, s, (float) (scrollX + this.x - i), (float) (scrollY + this.y + 9), -1);
            }
        } else {
            this.minecraft.font.drawShadow(poseStack, this.title, (float) (scrollX + this.x + 32), (float) (scrollY + this.y + 9), -1);

            if (s != null) {
                this.minecraft.font.drawShadow(poseStack, s, (float) (scrollX + this.x + this.width - i - 5), (float) (scrollY + this.y + 9), -1);
            }
        }

        int yOffset;
        if (drawTop) {
            yOffset = drawY + 26 - boxHeight + 7;
        } else {
            yOffset = scrollY + this.y + 9 + 17;
        }
        for (int k1 = 0; k1 < this.description.size(); ++k1) {
            this.minecraft.font.drawShadow(poseStack, this.description.get(k1), (float) (drawX + 5), (float) (yOffset + k1 * this.minecraft.font.lineHeight), -5592406);
        }
        if (this.criterionGrid != null && !CriterionGrid.requiresShift || Screen.hasShiftDown()) {
            int xOffset = drawX + 5;
            yOffset += this.description.size() * this.minecraft.font.lineHeight;
            for (int colIndex = 0; colIndex < this.criterionGrid.columns.size(); colIndex++) {
                CriterionGrid.Column col = this.criterionGrid.columns.get(colIndex);
                for (int rowIndex = 0; rowIndex < col.cells.size(); rowIndex++) {
                    this.minecraft.font.draw(poseStack, col.cells.get(rowIndex), xOffset, yOffset + rowIndex * this.minecraft.font.lineHeight, -5592406);
                }
                xOffset += col.width;
            }
        }

        this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.displayInfo.getIcon(), scrollX + this.x + 8, scrollY + this.y + 5);
    }

    protected void render9Sprite(PoseStack poseStack, int x, int y, int width, int height, int textureHeight, int textureWidth, int textureDistance, int textureX, int textureY) {
        // Top left corner
        this.blit(poseStack, x, y, textureX, textureY, textureHeight, textureHeight);
        // Top side
        RenderUtil.renderRepeating(this, poseStack, x + textureHeight, y, width - textureHeight - textureHeight, textureHeight, textureX + textureHeight, textureY, textureWidth - textureHeight - textureHeight, textureDistance);
        // Top right corner
        this.blit(poseStack, x + width - textureHeight, y, textureX + textureWidth - textureHeight, textureY, textureHeight, textureHeight);
        // Bottom left corner
        this.blit(poseStack, x, y + height - textureHeight, textureX, textureY + textureDistance - textureHeight, textureHeight, textureHeight);
        // Bottom side
        RenderUtil.renderRepeating(this, poseStack, x + textureHeight, y + height - textureHeight, width - textureHeight - textureHeight, textureHeight, textureX + textureHeight, textureY + textureDistance - textureHeight, textureWidth - textureHeight - textureHeight, textureDistance);
        // Bottom right corner
        this.blit(poseStack, x + width - textureHeight, y + height - textureHeight, textureX + textureWidth - textureHeight, textureY + textureDistance - textureHeight, textureHeight, textureHeight);
        // Left side
        RenderUtil.renderRepeating(this, poseStack, x, y + textureHeight, textureHeight, height - textureHeight - textureHeight, textureX, textureY + textureHeight, textureWidth, textureDistance - textureHeight - textureHeight);
        // Center
        RenderUtil.renderRepeating(this, poseStack, x + textureHeight, y + textureHeight, width - textureHeight - textureHeight, height - textureHeight - textureHeight, textureX + textureHeight, textureY + textureHeight, textureWidth - textureHeight - textureHeight, textureDistance - textureHeight - textureHeight);
        // Right side
        RenderUtil.renderRepeating(this, poseStack, x + width - textureHeight, y + textureHeight, textureHeight, height - textureHeight - textureHeight, textureX + textureWidth - textureHeight, textureY + textureHeight, textureWidth, textureDistance - textureHeight - textureHeight);
    }

    public boolean isMouseOver(double scrollX, double scrollY, double mouseX, double mouseY) {
        if (!this.displayInfo.isHidden() || this.advancementProgress != null && this.advancementProgress.isDone()) {
            double left = scrollX + this.x;
            double right = left + ADVANCEMENT_SIZE;
            double top = scrollY + this.y;
            double bottom = top + ADVANCEMENT_SIZE;
            return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
        } else {
            return false;
        }
    }

    public void attachToParent() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getFirstVisibleParent(this.advancement);

            if (this.parent != null) {
                this.parent.addGuiAdvancement(this);
            }
        }
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public Advancement getAdvancement() {
        return this.advancement;
    }
}
