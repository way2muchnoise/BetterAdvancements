package betteradvancements.common.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;

import java.util.Comparator;
import java.util.Optional;

public class AdvancementComparer {
    public static Comparator<AdvancementNode> sortByTitle() {
        return (n1, n2) -> {
            Advancement a1 = n1.advancement();
            Advancement a2 = n2.advancement();
            if (a1 == a2) {
                return 0;
            } else if (a1 == null && a2 != null) {
                return 1;
            } else if (a1 != null && a2 == null) {
                return -1;
            } else {
                Optional<DisplayInfo> info1 = a1.display();
                Optional<DisplayInfo> info2 = a2.display();

                if (info1.isEmpty() && info2.isEmpty()) {
                    return 0;
                } else if (info1.isEmpty() && info2.isPresent()) {
                    return 1;
                } else if (info1.isPresent() && info2.isEmpty()) {
                    return -1;
                } else {
                    String title1 = info1.get().getTitle().getString().toLowerCase();
                    String title2 = info2.get().getTitle().getString().toLowerCase();
                    return title1.compareTo(title2);
                }
            }
        };
    }
}
