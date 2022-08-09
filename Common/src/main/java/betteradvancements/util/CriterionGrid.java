package betteradvancements.util;

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
    private static final CriterionGrid empty = new CriterionGrid();

    private final List<String> cellContents;
    private final int[] cellWidths;
    private final int fontHeight;
    private final int numColumns;
    public final int numRows;
    public List<Column> columns;
    public int width;
    public int height;

    private CriterionGrid() {
        this.cellContents = Collections.emptyList();
        this.cellWidths = new int[0];
        this.fontHeight = 0;
        this.numColumns = 0;
        this.numRows = 0;
        this.columns = Collections.emptyList();
        this.width = 0;
        this.height = 0;
    }

    public CriterionGrid(List<String> cellContents, int[] cellWidths, int fontHeight, int numColumns) {
        this.cellContents = cellContents;
        this.cellWidths = cellWidths;
        this.fontHeight = fontHeight;
        this.numColumns = numColumns;
        this.numRows = (int)Math.ceil((double)cellContents.size() / numColumns);
    }

    public void init() {
        this.columns = new ArrayList<>();
        this.width = 0;
        for (int c = 0; c < this.numColumns; c++) {
            List<String> column = new ArrayList<>();
            int columnWidth = 0;
            for (int r = 0; r < this.numRows; r++) {
                int cellIndex = c * this.numRows + r;
                if (cellIndex >= this.cellContents.size()) {
                    break;
                }
                String str = this.cellContents.get(cellIndex);
                column.add(str);
                columnWidth = Math.max(columnWidth, this.cellWidths[cellIndex]);
            }
            this.columns.add(new Column(column, columnWidth));
            this.width += columnWidth;
        }
        this.height = this.numRows * this.fontHeight;
    }

    public class Column {
        public final List<String> cells;
        public final int width;

        public Column(List<String> cells, int width) {
            this.cells = cells;
            this.width = width;
        }
    }

    // Of all the possible grids whose aspect ratio is less than the maximum, this method returns the one with the smallest number of rows.
    // If there is no such grid, this method returns a single-column grid.
    public static CriterionGrid findOptimalCriterionGrid(Advancement advancement, AdvancementProgress progress, int maxWidth, Font font) {
        if (progress == null || progress.isDone() || detailLevel.equals(CriteriaDetail.OFF)) {
            return CriterionGrid.empty;
        }
        Map<String, Criterion> criteria = advancement.getCriteria();
        if (criteria.size() <= 1) {
            return CriterionGrid.empty;
        }
        int numUnobtained = 0;
        List<String> cellContents = new ArrayList<>();
        for (String criterion : criteria.keySet()) {
            CriterionProgress criterionProgress = progress.getCriterion(criterion);
            if (criterionProgress != null && criterionProgress.isDone()) {
                if (detailLevel.showObtained()) {
                    MutableComponent text = Component.literal(" + ").withStyle(ChatFormatting.GREEN);
                    MutableComponent text2 = Component.literal(criterion).withStyle(ChatFormatting.WHITE);
                    text.append(text2);
                    cellContents.add(text.getString());
                }
            }
            else {
                if (detailLevel.showUnobtained()) {
                    MutableComponent text = Component.literal(" x ").withStyle(ChatFormatting.DARK_RED);
                    MutableComponent text2 = Component.literal(criterion).withStyle(ChatFormatting.WHITE);
                	text.append(text2);
                    cellContents.add(text.getString());
                }
                numUnobtained++;
            }
        }

        if (!detailLevel.showUnobtained()) {
            MutableComponent text = Component.literal(" x ").withStyle(ChatFormatting.DARK_RED);
            MutableComponent text2 = Component.literal(numUnobtained + " remaining").withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC);
        	text.append(text2);
            cellContents.add(text.getString());
        }

        int[] cellWidths = new int[cellContents.size()];
        for (int i = 0; i < cellWidths.length; i++) {
            cellWidths[i] = font.width(cellContents.get(i));
        }

        int numCols = 0;
        CriterionGrid prevGrid = null;
        CriterionGrid currGrid = null;
        do {
            numCols++;
            CriterionGrid newGrid = new CriterionGrid(cellContents, cellWidths, font.lineHeight, numCols);
            if (prevGrid != null && newGrid.numRows == prevGrid.numRows) {
                // We increased the width without decreasing the height, which is pointless.
                continue;
            }
            newGrid.init();
            prevGrid = currGrid;
            currGrid = newGrid;
        } while(numCols <= cellContents.size() && currGrid.width <= maxWidth);
        return prevGrid != null ? prevGrid : currGrid;
    }
}
