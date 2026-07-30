package betteradvancements.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// An arrangement of criteria into rows and columns
public class CriterionGrid {
    public static CriteriaDetail detailLevel = CriteriaDetail.DEFAULT;
    public static boolean requiresShift = false;
    public static boolean colorWholeCriteriaText = false;
    public static int criteriaRotationSpeed = 3;
    private static final CriterionGrid empty = new CriterionGrid();

    private final List<Component> cellContents;
    private final int[] cellWidths;
    private final int fontHeight;
    private final int numColumns;
    private final int numRows;
    private final int numPages;
    private List<List<Column>> pagedColumns;
    public int width;
    public int height;

    private CriterionGrid() {
        this.cellContents = Collections.emptyList();
        this.cellWidths = new int[0];
        this.fontHeight = 0;
        this.numColumns = 0;
        this.numRows = 0;
        this.numPages = 1;
        this.pagedColumns = Collections.emptyList();
        this.width = 0;
        this.height = 0;
    }

    public CriterionGrid(List<Component> cellContents, int[] cellWidths, int fontHeight, int numColumns, int numPages) {
        this.cellContents = cellContents;
        this.cellWidths = cellWidths;
        this.fontHeight = fontHeight;
        this.numColumns = numColumns;
        this.numPages = numPages;
        this.numRows = (int)Math.ceil((double)cellContents.size() / numColumns / numPages);
    }

    public void init() {
        this.pagedColumns = new ArrayList<>();
        this.width = 0;
        for (int p = 0; p < this.numPages; p++) {
            List<Column> columns = new ArrayList<>();
            int pageWidth = 0;
            for (int c = 0; c < this.numColumns; c++) {
                List<Component> column = new ArrayList<>();
                int columnWidth = 0;
                for (int r = 0; r < this.numRows; r++) {
                    int cellIndex = p * this.numColumns * this.numRows + c * this.numRows + r;
                    if (cellIndex >= this.cellContents.size()) {
                        break;
                    }
                    column.add(this.cellContents.get(cellIndex));
                    columnWidth = Math.max(columnWidth, this.cellWidths[cellIndex]);
                }
                columns.add(new Column(column, columnWidth));
                pageWidth += columnWidth;
            }
            this.pagedColumns.add(columns);
            this.width = Math.max(pageWidth, this.width);
        }
        this.height = this.numRows * this.fontHeight;
    }

    public List<Column> getCurrentPage() {
        return this.pagedColumns.get((int) (System.currentTimeMillis() / (criteriaRotationSpeed * 1000) % this.numPages));
    }

    public boolean hasPages() {
        return !this.pagedColumns.isEmpty();
    }

    public record Column(List<Component> cells, int width) {}

    // Of all the possible grids whose aspect ratio is less than the maximum, this method returns the one with the smallest number of rows.
    // If there is no such grid, this method returns a single-column grid.
    public static CriterionGrid findOptimalCriterionGrid(Advancement advancement, AdvancementProgress progress, int maxWidth, int maxHeight, Font font) {
        if (progress == null || progress.isDone() || detailLevel.equals(CriteriaDetail.OFF)) {
            return CriterionGrid.empty;
        }
        Map<String, Criterion> criteria = advancement.getCriteria();
        if (criteria.size() <= 1) {
            return CriterionGrid.empty;
        }
        int numUnobtained = 0;
        List<Component> cellContents = new ArrayList<>();
        for (String criterion : criteria.keySet()) {
            CriterionProgress criterionProgress = progress.getCriterion(criterion);
            if (criterionProgress != null && criterionProgress.isDone()) {
                if (detailLevel.showObtained()) {
                    MutableComponent text = Component.literal(" + ").withStyle(ChatFormatting.GREEN);
                    MutableComponent text2 = CriterionTranslator.tryTranslateCriterion(holder, criterion).withStyle(colorWholeCriteriaText ? ChatFormatting.GREEN : ChatFormatting.WHITE);
                    text.append(text2);
                    cellContents.add(text);
                }
            }
            else {
                if (detailLevel.showUnobtained()) {
                    MutableComponent text = Component.literal(" x ").withStyle(ChatFormatting.DARK_RED);
                    MutableComponent text2 = CriterionTranslator.tryTranslateCriterion(holder, criterion).withStyle(colorWholeCriteriaText ? ChatFormatting.DARK_RED : ChatFormatting.WHITE);
                	text.append(text2);
                    cellContents.add(text);
                }
                numUnobtained++;
            }
        }

        if (!detailLevel.showUnobtained()) {
            MutableComponent text = Component.literal(" x ").withStyle(ChatFormatting.DARK_RED);
            MutableComponent text2 = Component.translatable("betteradvancements.remaining", numUnobtained).withStyle(colorWholeCriteriaText ? ChatFormatting.DARK_RED : ChatFormatting.WHITE, ChatFormatting.ITALIC);
        	text.append(text2);
            cellContents.add(text);
        }

        int[] cellWidths = new int[cellContents.size()];
        for (int i = 0; i < cellWidths.length; i++) {
            cellWidths[i] = font.width(cellContents.get(i));
        }

        int numCols = 0;
        int numPages = 1;
        CriterionGrid prevGrid = null;
        CriterionGrid currGrid = null;
        // Optimise columns
        do {
            numCols++;
            CriterionGrid newGrid = new CriterionGrid(cellContents, cellWidths, font.lineHeight, numCols, numPages);
            if (prevGrid != null && newGrid.numRows == prevGrid.numRows) {
                // We increased the width without decreasing the height, which is pointless.
                continue;
            }
            newGrid.init();
            prevGrid = currGrid;
            currGrid = newGrid;
        } while(numCols <= cellContents.size() && currGrid.width <= maxWidth);
        numCols--; // Decrease numCols as it will be one above what is accepted
        currGrid = prevGrid; // Make sure
        prevGrid = null; // Reset prevGrid before we work on pages
        // If needed further optimise for pages
        while(numPages <= cellContents.size() && currGrid.height > maxHeight) {
            numPages++;
            CriterionGrid newGrid = new CriterionGrid(cellContents, cellWidths, font.lineHeight, numCols, numPages);
            if (prevGrid != null && newGrid.numRows == prevGrid.numRows) {
                // We increased the pages without decreasing the height, which is pointless.
                continue;
            }
            newGrid.init();
            prevGrid = currGrid;
            currGrid = newGrid;
        }
        return currGrid;
    }
}
