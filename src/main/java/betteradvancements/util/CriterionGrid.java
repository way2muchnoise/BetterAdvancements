package betteradvancements.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.client.gui.FontRenderer;

public class CriterionGrid {
    private final List<String> cellContents;
    private final int[] cellWidths;
    private final int fontHeight;
    private final int numColumns;
    public final int numRows;
    public List<Column> columns;
    public int width;
    public int height;
    public double aspectRatio;

    public CriterionGrid() {
        this.cellContents = Collections.emptyList();
        this.cellWidths = new int[0];
        this.fontHeight = 0;
        this.numColumns = 0;
        this.numRows = 0;
        columns = Collections.emptyList();
        width = 0;
        height = 0;
        aspectRatio = Double.NaN;
    }

    public CriterionGrid(List<String> cellContents, int[] cellWidths, int fontHeight, int numColumns) {
        this.cellContents = cellContents;
        this.cellWidths = cellWidths;
        this.fontHeight = fontHeight;
        this.numColumns = numColumns;
        this.numRows = (int)Math.ceil((double)cellContents.size() / numColumns);
    }
    public void init() {
        List<Column> columns = new ArrayList<Column>();
        int widthSum = 0;
        for (int c = 0; c < numColumns; c++) {
            List<String> column = new ArrayList<String>();
            int columnWidth = 0;
            for (int r = 0; r < numRows; r++) {
                int cellIndex = c * numRows + r;
                if (cellIndex >= cellContents.size()) {
                    break;
                }
                String str = cellContents.get(cellIndex);
                column.add(str);
                columnWidth = Math.max(columnWidth, cellWidths[cellIndex]);
            }
            columns.add(new Column(column, columnWidth));
            widthSum += columnWidth;
        }
        this.columns = columns;
        this.width = widthSum;
        this.height = numRows * fontHeight;
        this.aspectRatio = width / height;
    }

    public class Column {
        public final List<String> cells;
        public final int width;

        public Column(List<String> cells, int width) {
            this.cells = cells;
            this.width = width;
        }
    }

    public static CriterionGrid findOptimalCriterionGrid(Advancement advancement, AdvancementProgress progress, double maxAspectRatio, FontRenderer renderer) {
        if (progress == null || progress.isDone()) {
            return new CriterionGrid();
        }
        Map<String, Criterion> criteria = advancement.getCriteria();
        if (criteria.size() <= 1) {
            return new CriterionGrid();
        }
        boolean anyObtained = false;
        int numUnobtained = 0;
        List<String> cellContents = new ArrayList<String>();
        for (String criterion : criteria.keySet()) {
            if (progress.getCriterionProgress(criterion).isObtained()) {
                cellContents.add(" §2+§  " + criterion);
                anyObtained = true;
            }
            else {
                numUnobtained++;
            }
        }
        if (!anyObtained) {
            return new CriterionGrid();
        }
        cellContents.add(" §4x§  §o" + numUnobtained + " remaining");

        int[] cellWidths = new int[cellContents.size()];
        for (int i = 0; i < cellWidths.length; i++) {
            cellWidths[i] = renderer.getStringWidth(cellContents.get(i));
        }

        CriterionGrid prevGrid = null;
        for (int numCols = 1; numCols <= cellContents.size(); numCols++)
        {
            CriterionGrid currGrid = new CriterionGrid(cellContents, cellWidths, renderer.FONT_HEIGHT, numCols);
            if (prevGrid != null && currGrid.numRows == prevGrid.numRows)
                continue; // We increased the width without decreasing the height, which is pointless.
            currGrid.init();
            if (currGrid.aspectRatio > maxAspectRatio) {
                if (prevGrid == null) {
                    prevGrid = currGrid;
                }
                break;
            }
            prevGrid = currGrid;
        }
        return prevGrid;
    }
}
