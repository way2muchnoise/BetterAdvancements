package betteradvancements.util;

import java.util.List;

public class CriterionColumn {
    public final List<String> cells;
    public final int width;
    
    public CriterionColumn(List<String> cells, int width) {
        this.cells = cells;
        this.width = width;
    }
}
