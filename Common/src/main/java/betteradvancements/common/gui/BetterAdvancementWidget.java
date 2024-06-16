package betteradvancements.common.gui;

import betteradvancements.common.advancements.BetterDisplayInfo;
import betteradvancements.common.api.IBetterAdvancementEntryGui;
import betteradvancements.common.api.event.IAdvancementDrawConnectionsEvent;
import betteradvancements.common.platform.Services;
import betteradvancements.common.reference.Resources;
import betteradvancements.common.util.CriterionGrid;
import betteradvancements.common.util.RenderUtil;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.Collections;
import java.util.List;

public class BetterAdvancementWidget implements IBetterAdvancementEntryGui {
    protected static final int ADVANCEMENT_SIZE = 26;
    private static final int CORNER_SIZE = 10;
    private static final int WIDGET_WIDTH = 256, WIDGET_HEIGHT = 26, TITLE_SIZE = 32, ICON_OFFSET = 128, ICON_SIZE = 26;

    private final BetterAdvancementTab betterAdvancementTabGui;
    private final AdvancementNode advancementNode;
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

    public BetterAdvancementWidget(BetterAdvancementTab betterAdvancementTabGui, Minecraft mc, AdvancementNode advancementNode, DisplayInfo displayInfo) {
        this.betterAdvancementTabGui = betterAdvancementTabGui;
        this.advancementNode = advancementNode;
        this.betterDisplayInfo = betterAdvancementTabGui.getBetterDisplayInfo(this.advancementNode);
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
        if (this.advancementNode.advancement().criteria().size() > 1) {
            // Add some space for the requirement counter
            int strLengthRequirementCount = String.valueOf(this.advancementNode.advancement().criteria().size()).length();
            k = mc.font.width("  ") + mc.font.width("0") * strLengthRequirementCount * 2 + mc.font.width("/");
        }
        int titleWidth = 29 + mc.font.width(this.title) + k;
        BetterAdvancementsScreen screen = betterAdvancementTabGui.getScreen();
        this.criterionGrid = CriterionGrid.findOptimalCriterionGrid(this.advancementNode.advancement(), advancementProgress, screen.width / 2, mc.font);
        int maxWidth;
        
        if (!CriterionGrid.requiresShift || Screen.hasShiftDown()) {
            maxWidth = Math.max(titleWidth, this.criterionGrid.width);
        }
        else {
            maxWidth =  titleWidth;
        }
        this.description = Language.getInstance().getVisualOrder(
            this.findOptimalLines(ComponentUtils.mergeStyles(
                displayInfo.getDescription().copy(),
                Style.EMPTY.withColor(displayInfo.getType().getChatColor())
            ), maxWidth));

        for (FormattedCharSequence line : this.description) {
            maxWidth = Math.max(maxWidth, mc.font.width(line));
        }

        this.width = maxWidth + 8;
    }

    private List<FormattedText> findOptimalLines(Component line, int width) {
        if (line.getString().isEmpty()) {
            return Collections.emptyList();
        } else {
            StringSplitter stringsplitter = this.minecraft.font.getSplitter();
            List<FormattedText> list = stringsplitter.splitLines(line, width, Style.EMPTY);
            if (list.size() > 1) {
                width = Math.max(width, betterAdvancementTabGui.getScreen().internalWidth / 4);
                list = stringsplitter.splitLines(line, width, Style.EMPTY);
            }
            while (list.size() > 5 && width < WIDGET_WIDTH * 1.5 && width < betterAdvancementTabGui.getScreen().internalWidth / 2.5) {
                width += width / 4;
                list = stringsplitter.splitLines(line, width, Style.EMPTY);
            }
            return list;
        }
    }

    private BetterAdvancementWidget getFirstVisibleParent(AdvancementNode advancement) {
        do {
            advancement = advancement.parent();
        } while(advancement != null && advancement.advancement().display().isEmpty());

        if (advancement != null && !advancement.advancement().display().isEmpty()) {
            return this.betterAdvancementTabGui.getWidget(advancement.holder());
        } else {
            return null;
        }
    }

    public void drawConnectivity(GuiGraphics guiGraphics, int scrollX, int scrollY, boolean drawInside) {
        //Check if connections should be drawn at all
        if (!this.betterDisplayInfo.hideLines()) {
            //Draw connection to parent
            if (this.parent != null) {
                
                this.drawConnection(guiGraphics, this.parent, scrollX, scrollY, drawInside);
            }
            
            //Create and post event to get extra connections
            IAdvancementDrawConnectionsEvent event = Services.PLATFORM.getEventHelper().postAdvancementDrawConnectionsEvent(this.advancementNode);

            //Draw extra connections from event
            for (AdvancementHolder parent : event.getExtraConnections()) {
                final BetterAdvancementWidget parentGui = this.betterAdvancementTabGui.getWidget(parent);
                
                if (parentGui != null) {
                    this.drawConnection(guiGraphics, parentGui, scrollX, scrollY, drawInside);
                }
            }
        }
        //Draw child connections
        for (BetterAdvancementWidget betterAdvancementWidget : this.children) {
            betterAdvancementWidget.drawConnectivity(guiGraphics, scrollX, scrollY, drawInside);
        }
    }
    
    /**
     * Draws connection line between this advancement and the advancement supplied in parent.
     */
    public void drawConnection(GuiGraphics guiGraphics, BetterAdvancementWidget parent, int scrollX, int scrollY, boolean drawInside) {
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
                guiGraphics.hLine(endXHalf, startX, startY - 1, borderLineColor);
                guiGraphics.hLine(endXHalf + 1, startX, startY, borderLineColor);
                guiGraphics.hLine(endXHalf, startX, startY + 1, borderLineColor);
                guiGraphics.hLine(endX, endXHalf - 1, endY - 1, borderLineColor);
                guiGraphics.hLine(endX, endXHalf - 1, endY, borderLineColor);
                guiGraphics.hLine(endX, endXHalf - 1, endY + 1, borderLineColor);
                guiGraphics.vLine(endXHalf - 1, endY, startY, borderLineColor);
                guiGraphics.vLine(endXHalf + 1, endY, startY, borderLineColor);
            } else {
                guiGraphics.hLine(endXHalf, startX, startY, innerLineColor);
                guiGraphics.hLine(endX, endXHalf, endY, innerLineColor);
                guiGraphics.vLine(endXHalf, endY, startY, innerLineColor);
            }
        }
    }

    public void draw(GuiGraphics guiGraphics, int scrollX, int scrollY) {
        if (!this.displayInfo.isHidden() || this.advancementProgress != null && this.advancementProgress.isDone()) {
            float f = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
            AdvancementWidgetType advancementState;

            if (f >= 1.0F) {
                advancementState = AdvancementWidgetType.OBTAINED;
            } else {
                advancementState = AdvancementWidgetType.UNOBTAINED;
            }

            RenderUtil.setColor(betterDisplayInfo.getIconColor(advancementState));
            RenderSystem.enableBlend();
            guiGraphics.blitSprite(advancementState.frameSprite(this.displayInfo.getType()), scrollX + this.x + 3, scrollY + this.y, ICON_SIZE, ICON_SIZE);
            RenderUtil.setColor(betterDisplayInfo.defaultIconColor());
            guiGraphics.renderFakeItem(this.displayInfo.getIcon(), scrollX + this.x + 8, scrollY + this.y + 5);
        }

        for (BetterAdvancementWidget betterAdvancementWidget : this.children) {
            betterAdvancementWidget.draw(guiGraphics, scrollX, scrollY);
        }
    }

    public void getAdvancementProgress(AdvancementProgress advancementProgressIn) {
        this.advancementProgress = advancementProgressIn;
        this.refreshHover();
    }

    public void addGuiAdvancement(BetterAdvancementWidget betterAdvancementEntryScreen) {
        this.children.add(betterAdvancementEntryScreen);
    }

    public void drawHover(GuiGraphics guiGraphics, int scrollX, int scrollY, float fade, int left, int top) {
        this.refreshHover();
        boolean drawLeft = left + scrollX + this.x + this.width + ADVANCEMENT_SIZE >= this.betterAdvancementTabGui.getScreen().internalWidth;
        String s = this.advancementProgress == null || this.advancementProgress.getProgressText() == null ? null : this.advancementProgress.getProgressText().getString();
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
                this.render9Sprite(guiGraphics, drawX, drawY + ADVANCEMENT_SIZE - boxHeight, this.width, boxHeight, CORNER_SIZE, WIDGET_WIDTH, WIDGET_HEIGHT, 0, 52);
            } else {
                this.render9Sprite(guiGraphics, drawX, drawY, this.width, boxHeight, CORNER_SIZE, WIDGET_WIDTH, WIDGET_HEIGHT, 0, 52);
            }
        }

        // Title left side
        RenderUtil.setColor(betterDisplayInfo.getTitleColor(stateTitleLeft));
        int left_side = Math.min(j, WIDGET_WIDTH - 16);
        guiGraphics.blit(Resources.Gui.WIDGETS, drawX, drawY, 0, betterDisplayInfo.getTitleYMultiplier(stateTitleLeft) * WIDGET_HEIGHT, left_side, WIDGET_HEIGHT);
        if (left_side < j) {
            guiGraphics.blit(Resources.Gui.WIDGETS, drawX + left_side, drawY, 16, betterDisplayInfo.getTitleYMultiplier(stateTitleLeft) * WIDGET_HEIGHT, j - left_side, WIDGET_HEIGHT);
        }
        // Title right side
        RenderUtil.setColor(betterDisplayInfo.getTitleColor(stateTitleRight));
        int right_side = Math.min(k, WIDGET_WIDTH - 16);
        guiGraphics.blit(Resources.Gui.WIDGETS, drawX + j, drawY, WIDGET_WIDTH - right_side, betterDisplayInfo.getTitleYMultiplier(stateTitleRight) * WIDGET_HEIGHT, right_side, WIDGET_HEIGHT);
        if (right_side < k) {
            // + and - 2 is to create some overlap in the drawing when it extends past the max length of the texture
            guiGraphics.blit(Resources.Gui.WIDGETS, drawX + j + right_side - 2, drawY, WIDGET_WIDTH - k + right_side - 2, betterDisplayInfo.getTitleYMultiplier(stateTitleRight) * WIDGET_HEIGHT, k - right_side + 2, WIDGET_HEIGHT);
        }
        // Advancement icon
        RenderUtil.setColor(betterDisplayInfo.getIconColor(stateIcon));
        guiGraphics.blitSprite(stateIcon.frameSprite(this.displayInfo.getType()), scrollX + this.x + 3, scrollY + this.y, ICON_SIZE, ICON_SIZE);
        RenderUtil.setColor(betterDisplayInfo.defaultIconColor());

        if (drawLeft) {
            guiGraphics.drawString(this.minecraft.font, this.title, drawX + 5, scrollY + this.y + 9, -1);

            if (s != null) {
                guiGraphics.drawString(this.minecraft.font, s, scrollX + this.x - i, scrollY + this.y + 9, -1);
            }
        } else {
            guiGraphics.drawString(this.minecraft.font, this.title, scrollX + this.x + 32, scrollY + this.y + 9, -1);

            if (s != null) {
                guiGraphics.drawString(this.minecraft.font, s, scrollX + this.x + this.width - i - 5, scrollY + this.y + 9, -1);
            }
        }

        int yOffset;
        if (drawTop) {
            yOffset = drawY + 26 - boxHeight + 7;
        } else {
            yOffset = scrollY + this.y + 9 + 17;
        }
        for (int k1 = 0; k1 < this.description.size(); ++k1) {
            guiGraphics.drawString(this.minecraft.font, this.description.get(k1), drawX + 5, yOffset + k1 * this.minecraft.font.lineHeight, -5592406, false);
        }
        if (this.criterionGrid != null && !CriterionGrid.requiresShift || Screen.hasShiftDown()) {
            int xOffset = drawX + 5;
            yOffset += this.description.size() * this.minecraft.font.lineHeight;
            for (int colIndex = 0; colIndex < this.criterionGrid.columns.size(); colIndex++) {
                CriterionGrid.Column col = this.criterionGrid.columns.get(colIndex);
                for (int rowIndex = 0; rowIndex < col.cells().size(); rowIndex++) {
                    guiGraphics.drawString(this.minecraft.font, col.cells().get(rowIndex), xOffset, yOffset + rowIndex * this.minecraft.font.lineHeight, -5592406, false);
                }
                xOffset += col.width();
            }
        }

        guiGraphics.renderFakeItem(this.displayInfo.getIcon(), scrollX + this.x + 8, scrollY + this.y + 5);
    }

    protected void render9Sprite(GuiGraphics guiGraphics, int x, int y, int width, int height, int textureHeight, int textureWidth, int textureDistance, int textureX, int textureY) {
        // Top left corner
        guiGraphics.blit(Resources.Gui.WIDGETS, x, y, textureX, textureY, textureHeight, textureHeight);
        // Top side
        RenderUtil.renderRepeating(Resources.Gui.WIDGETS, guiGraphics, x + textureHeight, y, width - textureHeight - textureHeight, textureHeight, textureX + textureHeight, textureY, textureWidth - textureHeight - textureHeight, textureDistance);
        // Top right corner
        guiGraphics.blit(Resources.Gui.WIDGETS, x + width - textureHeight, y, textureX + textureWidth - textureHeight, textureY, textureHeight, textureHeight);
        // Bottom left corner
        guiGraphics.blit(Resources.Gui.WIDGETS, x, y + height - textureHeight, textureX, textureY + textureDistance - textureHeight, textureHeight, textureHeight);
        // Bottom side
        RenderUtil.renderRepeating(Resources.Gui.WIDGETS, guiGraphics, x + textureHeight, y + height - textureHeight, width - textureHeight - textureHeight, textureHeight, textureX + textureHeight, textureY + textureDistance - textureHeight, textureWidth - textureHeight - textureHeight, textureDistance);
        // Bottom right corner
        guiGraphics.blit(Resources.Gui.WIDGETS, x + width - textureHeight, y + height - textureHeight, textureX + textureWidth - textureHeight, textureY + textureDistance - textureHeight, textureHeight, textureHeight);
        // Left side
        RenderUtil.renderRepeating(Resources.Gui.WIDGETS, guiGraphics, x, y + textureHeight, textureHeight, height - textureHeight - textureHeight, textureX, textureY + textureHeight, textureWidth, textureDistance - textureHeight - textureHeight);
        // Center
        RenderUtil.renderRepeating(Resources.Gui.WIDGETS, guiGraphics, x + textureHeight, y + textureHeight, width - textureHeight - textureHeight, height - textureHeight - textureHeight, textureX + textureHeight, textureY + textureHeight, textureWidth - textureHeight - textureHeight, textureDistance - textureHeight - textureHeight);
        // Right side
        RenderUtil.renderRepeating(Resources.Gui.WIDGETS, guiGraphics, x + width - textureHeight, y + textureHeight, textureHeight, height - textureHeight - textureHeight, textureX + textureWidth - textureHeight, textureY + textureHeight, textureWidth, textureDistance - textureHeight - textureHeight);
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
        if (this.parent == null && advancementNode.advancement().parent().isPresent()) {
            this.parent = this.getFirstVisibleParent(advancementNode);

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
    public AdvancementNode getAdvancement() {
        return this.advancementNode;
    }
}
