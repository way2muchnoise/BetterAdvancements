package betteradvancements.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;

import java.util.Comparator;

public class AdvancementComparer {
    public static Comparator<Advancement> sortByTitle() {
        return (a1, a2) -> {
            if (a1 == a2) {
                return 0;
            } else if (a1 == null && a2 != null) {
                return 1;
            } else if (a1 != null && a2 == null) {
                return -1;
            } else {
                DisplayInfo info1 = a1.getDisplay();
                DisplayInfo info2 = a2.getDisplay();

                if (info1 == info2) {
                    return 0;
                } else if (info1 == null && info2 != null) {
                    return 1;
                } else if (info1 != null && info2 == null) {
                    return -1;
                } else {
                    String title1 = info1.getTitle().getString().toLowerCase();
                    String title2 = info2.getTitle().getString().toLowerCase()
                        ;
                    return title1.compareTo(title2);
                }
            }
        };
    }
}
