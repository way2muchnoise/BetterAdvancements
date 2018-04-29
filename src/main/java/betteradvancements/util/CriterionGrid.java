package betteradvancements.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CriterionGrid {
    private final List<String> cellContents;
    private final int[] cellWidths;
    private final int fontHeight;
    private final int numColumns;
    public final int numRows;
    public List<CriterionColumn> columns;
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
        List<CriterionColumn> columns = new ArrayList<CriterionColumn>();
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
            columns.add(new CriterionColumn(column, columnWidth));
            widthSum += columnWidth;
        }
        this.columns = columns;
        this.width = widthSum;
        this.height = numRows * fontHeight;
        this.aspectRatio = width / height;
    }
}
