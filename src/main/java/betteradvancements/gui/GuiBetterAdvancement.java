package betteradvancements.gui;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.advancements.BetterDisplayInfoRegistry;
import betteradvancements.api.event.AdvancementDrawConnectionsEvent;
import betteradvancements.reference.Resources;
import betteradvancements.util.RenderUtil;
import com.google.common.collect.Lists;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.advancements.AdvancementState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GLSync;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class GuiBetterAdvancement extends Gui {
    private static final int ADVANCEMENT_SIZE = 26, CORNER_SIZE = 10;
    private static final int WIDGET_WIDTH = 200, WIDGET_HEIGHT = 20, TITLE_SIZE = 32;
    private static final Pattern PATTERN = Pattern.compile("(.+) \\S+");

    private final GuiBetterAdvancementTab guiBetterAdvancementTab;
    private final Advancement advancement;
    private final BetterDisplayInfo betterDisplayInfo;
    private final DisplayInfo displayInfo;
    private final String title;
    private final int width;
    private final List<String> description;
    private final Minecraft minecraft;
    private GuiBetterAdvancement parent;
    private final List<GuiBetterAdvancement> children = Lists.newArrayList();
    private AdvancementProgress advancementProgress;
    private final int x, y;
    private final int screenScale;

    public GuiBetterAdvancement(GuiBetterAdvancementTab guiBetterAdvancementTab, Minecraft mc, Advancement advancement, DisplayInfo displayInfo) {
        this.guiBetterAdvancementTab = guiBetterAdvancementTab;
        this.advancement = advancement;
        this.betterDisplayInfo = guiBetterAdvancementTab.getBetterDisplayInfo(advancement);
        this.displayInfo = displayInfo;
        this.minecraft = mc;
        this.title = mc.fontRenderer.trimStringToWidth(displayInfo.getTitle().getFormattedText(), 163);
        this.x = MathHelper.floor(displayInfo.getX() * 32.0F);
        this.y = MathHelper.floor(displayInfo.getY() * 27.0F);
        int k = 0;
        if (advancement.getRequirementCount() > 1) {
            // Add some space for the requirement counter
            int strLengthRequirementCount = String.valueOf(advancement.getRequirementCount()).length();
            k = mc.fontRenderer.getStringWidth("  ") + mc.fontRenderer.getStringWidth("0") * strLengthRequirementCount * 2 + mc.fontRenderer.getStringWidth("/");
        }
        int titleWidth = 29 + mc.fontRenderer.getStringWidth(this.title) + k;
        String s = displayInfo.getDescription().getFormattedText();
        this.description = this.findOptimalLines(s, titleWidth);

        for (String line : this.description) {
            titleWidth = Math.max(titleWidth, mc.fontRenderer.getStringWidth(line));
        }

        this.width = titleWidth + 8;
        this.screenScale = new ScaledResolution(mc).getScaleFactor();
    }

    private List<String> findOptimalLines(String line, int width) {
        if (line.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> list = this.minecraft.fontRenderer.listFormattedStringToWidth(line, width);
            if (list.size() > 1) {
                width = Math.max(width, guiBetterAdvancementTab.getScreen().width / 4);
                list = this.minecraft.fontRenderer.listFormattedStringToWidth(line, width);
            }
            while (list.size() > 5 && width < WIDGET_WIDTH * 1.5 && width < guiBetterAdvancementTab.getScreen().width / 2) {
                width += width / 4;
                list = this.minecraft.fontRenderer.listFormattedStringToWidth(line, width);
            }
            return list;
        }
    }

    @Nullable
    private GuiBetterAdvancement getFirstVisibleParent(Advancement advancementIn) {
        while (true) {
            advancementIn = advancementIn.getParent();

            if (advancementIn == null || advancementIn.getDisplay() != null) {
                break;
            }
        }

        if (advancementIn != null && advancementIn.getDisplay() != null) {
            return this.guiBetterAdvancementTab.getAdvancementGui(advancementIn);
        } else {
            return null;
        }
    }

    public void drawConnectivity(int scrollX, int scrollY, boolean drawInside) {
        //Draw connection to parent
        if (this.parent != null) {
            
            this.drawConnection(this.parent, scrollX, scrollY, drawInside);
        }
        
        //Create and post event to get extra connections
        final AdvancementDrawConnectionsEvent event = new AdvancementDrawConnectionsEvent(this.advancement);
        MinecraftForge.EVENT_BUS.post(event);
        
        //Draw extra connections from event
        for (Advancement parent : event.getExtraConnections()) {
            final GuiBetterAdvancement parentGui = this.guiBetterAdvancementTab.getAdvancementGui(parent);
            
            if (parentGui != null) {
                this.drawConnection(parentGui, scrollX, scrollY, drawInside);
            }
        }

        //Draw child connections
        for (GuiBetterAdvancement guiBetterAdvancement : this.children) {
            guiBetterAdvancement.drawConnectivity(scrollX, scrollY, drawInside);
        }
    }
    
    /**
     * Draws connection line between this advancement and the advancement supplied in parent.
     */
    public void drawConnection(GuiBetterAdvancement parent, int scrollX, int scrollY, boolean drawInside) {
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
                case 1: {
                    width = drawInside ? 1.5 : 0.5;
                    break;
                }
                case 2: {
                    width = drawInside ? 2.25 : 0.75;
                    break;
                }
                case 3: {
                    width = drawInside ? 2 : 0.6666666666666667;
                    break;
                }
                case 4: {
                    width = drawInside ? 2.125 : 0.625;
                    break;
                }
                default: {
                    width = drawInside ? 3 : 1;
                    break;
                }
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
                this.drawHorizontalLine(endXHalf, startX, startY - 1, borderLineColor);
                this.drawHorizontalLine(endXHalf + 1, startX, startY, borderLineColor);
                this.drawHorizontalLine(endXHalf, startX, startY + 1, borderLineColor);
                this.drawHorizontalLine(endX, endXHalf - 1, endY - 1, borderLineColor);
                this.drawHorizontalLine(endX, endXHalf - 1, endY, borderLineColor);
                this.drawHorizontalLine(endX, endXHalf - 1, endY + 1, borderLineColor);
                this.drawVerticalLine(endXHalf - 1, endY, startY, borderLineColor);
                this.drawVerticalLine(endXHalf + 1, endY, startY, borderLineColor);
            } else {
                this.drawHorizontalLine(endXHalf, startX, startY, innerLineColor);
                this.drawHorizontalLine(endX, endXHalf, endY, innerLineColor);
                this.drawVerticalLine(endXHalf, endY, startY, innerLineColor);
            }
        }
    }

    public void draw(int scrollX, int scrollY) {
        if (!this.displayInfo.isHidden() || this.advancementProgress != null && this.advancementProgress.isDone()) {
            float f = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
            AdvancementState advancementstate;

            if (f >= 1.0F) {
                advancementstate = AdvancementState.OBTAINED;
            } else {
                advancementstate = AdvancementState.UNOBTAINED;
            }

            this.minecraft.getTextureManager().bindTexture(Resources.Gui.WIDGETS);
            RenderUtil.setColor(advancementstate == AdvancementState.OBTAINED ? betterDisplayInfo.getCompletedIconColor() : betterDisplayInfo.getUnCompletedIconColor());
            GlStateManager.enableBlend();
            this.drawTexturedModalRect(scrollX + this.x + 3, scrollY + this.y, this.displayInfo.getFrame().getIcon(), 128 + 26, 26, 26);
            RenderHelper.enableGUIStandardItemLighting();
            this.minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, this.displayInfo.getIcon(), scrollX + this.x + 8, scrollY + this.y + 5);
        }

        for (GuiBetterAdvancement guiBetterAdvancement : this.children) {
            guiBetterAdvancement.draw(scrollX, scrollY);
        }
    }

    public void getAdvancementProgress(AdvancementProgress advancementProgressIn) {
        this.advancementProgress = advancementProgressIn;
    }

    public void addGuiAdvancement(GuiBetterAdvancement guiBetterAdvancement) {
        this.children.add(guiBetterAdvancement);
    }

    public void drawHover(int scrollX, int scrollY, float fade, int left, int top) {
        boolean drawLeft = left + scrollX + this.x + this.width + ADVANCEMENT_SIZE >= this.guiBetterAdvancementTab.getScreen().width;
        String s = this.advancementProgress == null ? null : this.advancementProgress.getProgressText();
        int i = s == null ? 0 : this.minecraft.fontRenderer.getStringWidth(s);
        boolean drawTop = top + scrollY + this.y + this.description.size() * this.minecraft.fontRenderer.FONT_HEIGHT + 50 >= this.guiBetterAdvancementTab.getScreen().height;
        float percentageObtained = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
        int j = MathHelper.floor(percentageObtained * (float) this.width);
        AdvancementState advancementstate;
        AdvancementState advancementstate1;
        AdvancementState advancementstate2;

        if (percentageObtained >= 1.0F) {
            j = this.width / 2;
            advancementstate = AdvancementState.OBTAINED;
            advancementstate1 = AdvancementState.OBTAINED;
            advancementstate2 = AdvancementState.OBTAINED;
        } else if (j < 2) {
            j = this.width / 2;
            advancementstate = AdvancementState.UNOBTAINED;
            advancementstate1 = AdvancementState.UNOBTAINED;
            advancementstate2 = AdvancementState.UNOBTAINED;
        } else if (j > this.width - 2) {
            j = this.width / 2;
            advancementstate = AdvancementState.OBTAINED;
            advancementstate1 = AdvancementState.OBTAINED;
            advancementstate2 = AdvancementState.UNOBTAINED;
        } else {
            advancementstate = AdvancementState.OBTAINED;
            advancementstate1 = AdvancementState.UNOBTAINED;
            advancementstate2 = AdvancementState.UNOBTAINED;
        }

        int k = this.width - j;
        this.minecraft.getTextureManager().bindTexture(Resources.Gui.WIDGETS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        int drawY = scrollY + this.y;
        int drawX;

        if (drawLeft) {
            drawX = scrollX + this.x - this.width + ADVANCEMENT_SIZE + 6;
        } else {
            drawX = scrollX + this.x;
        }

        int boxHeight = TITLE_SIZE + this.description.size() * this.minecraft.fontRenderer.FONT_HEIGHT;

        if (!this.description.isEmpty()) {
            if (drawTop) {
                this.render9Sprite(drawX, drawY + ADVANCEMENT_SIZE - boxHeight, this.width, boxHeight, CORNER_SIZE, WIDGET_WIDTH, 26, 0, 52);
            } else {
                this.render9Sprite(drawX, drawY, this.width, boxHeight, CORNER_SIZE, WIDGET_WIDTH, 26, 0, 52);
            }
        }

        // Title left side
        RenderUtil.setColor(advancementstate == AdvancementState.OBTAINED ? betterDisplayInfo.getCompletedTitleColor() : betterDisplayInfo.getUnCompletedTitleColor());
        this.drawTexturedModalRect(drawX, drawY, 0, 3 * 26, j, 26);
        // Title right side
        RenderUtil.setColor(advancementstate1 == AdvancementState.OBTAINED ? betterDisplayInfo.getCompletedTitleColor() : betterDisplayInfo.getUnCompletedTitleColor());
        this.drawTexturedModalRect(drawX + j, drawY, WIDGET_WIDTH - k, 3 * 26, k, 26);
        // Advancement icon
        RenderUtil.setColor(advancementstate2 == AdvancementState.OBTAINED ? betterDisplayInfo.getCompletedIconColor() : betterDisplayInfo.getUnCompletedIconColor());
        this.drawTexturedModalRect(scrollX + this.x + 3, scrollY + this.y, this.displayInfo.getFrame().getIcon(), 128 + 26, 26, 26);

        if (drawLeft) {
            this.minecraft.fontRenderer.drawString(this.title, (float) (drawX + 5), (float) (scrollY + this.y + 9), -1, true);

            if (s != null) {
                this.minecraft.fontRenderer.drawString(s, (float) (scrollX + this.x - i), (float) (scrollY + this.y + 9), -1, true);
            }
        } else {
            this.minecraft.fontRenderer.drawString(this.title, (float) (scrollX + this.x + 32), (float) (scrollY + this.y + 9), -1, true);

            if (s != null) {
                this.minecraft.fontRenderer.drawString(s, (float) (scrollX + this.x + this.width - i - 5), (float) (scrollY + this.y + 9), -1, true);
            }
        }

        if (drawTop) {
            for (int k1 = 0; k1 < this.description.size(); ++k1) {
                this.minecraft.fontRenderer.drawString(this.description.get(k1), (float) (drawX + 5), (float) (drawY + 26 - boxHeight + 7 + k1 * this.minecraft.fontRenderer.FONT_HEIGHT), -5592406, false);
            }
        } else {
            for (int l1 = 0; l1 < this.description.size(); ++l1) {
                this.minecraft.fontRenderer.drawString(this.description.get(l1), (float) (drawX + 5), (float) (scrollY + this.y + 9 + 17 + l1 * this.minecraft.fontRenderer.FONT_HEIGHT), -5592406, false);
            }
        }

        RenderHelper.enableGUIStandardItemLighting();
        this.minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, this.displayInfo.getIcon(), scrollX + this.x + 8, scrollY + this.y + 5);
    }

    protected void render9Sprite(int x, int y, int width, int height, int textureHeight, int textureWidth, int textureDistance, int textureX, int textureY) {
        // Top left corner
        this.drawTexturedModalRect(x, y, textureX, textureY, textureHeight, textureHeight);
        // Top side
        RenderUtil.renderRepeating(this,x + textureHeight, y, width - textureHeight - textureHeight, textureHeight, textureX + textureHeight, textureY, textureWidth - textureHeight - textureHeight, textureDistance);
        // Top right corner
        this.drawTexturedModalRect(x + width - textureHeight, y, textureX + textureWidth - textureHeight, textureY, textureHeight, textureHeight);
        // Bottom left corner
        this.drawTexturedModalRect(x, y + height - textureHeight, textureX, textureY + textureDistance - textureHeight, textureHeight, textureHeight);
        // Bottom side
        RenderUtil.renderRepeating(this,x + textureHeight, y + height - textureHeight, width - textureHeight - textureHeight, textureHeight, textureX + textureHeight, textureY + textureDistance - textureHeight, textureWidth - textureHeight - textureHeight, textureDistance);
        // Bottom right corner
        this.drawTexturedModalRect(x + width - textureHeight, y + height - textureHeight, textureX + textureWidth - textureHeight, textureY + textureDistance - textureHeight, textureHeight, textureHeight);
        // Left side
        RenderUtil.renderRepeating(this,x, y + textureHeight, textureHeight, height - textureHeight - textureHeight, textureX, textureY + textureHeight, textureWidth, textureDistance - textureHeight - textureHeight);
        // Center
        RenderUtil.renderRepeating(this,x + textureHeight, y + textureHeight, width - textureHeight - textureHeight, height - textureHeight - textureHeight, textureX + textureHeight, textureY + textureHeight, textureWidth - textureHeight - textureHeight, textureDistance - textureHeight - textureHeight);
        // Right side
        RenderUtil.renderRepeating(this,x + width - textureHeight, y + textureHeight, textureHeight, height - textureHeight - textureHeight, textureX + textureWidth - textureHeight, textureY + textureHeight, textureWidth, textureDistance - textureHeight - textureHeight);
    }

    public boolean isMouseOver(int scrollX, int scrollY, int mouseX, int mouseY) {
        if (!this.displayInfo.isHidden() || this.advancementProgress != null && this.advancementProgress.isDone()) {
            int left = scrollX + this.x;
            int right = left + ADVANCEMENT_SIZE;
            int top = scrollY + this.y;
            int bottom = top + ADVANCEMENT_SIZE;
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

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}
