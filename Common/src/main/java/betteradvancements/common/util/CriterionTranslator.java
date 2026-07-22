package betteradvancements.common.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CriterionTranslator {
    public static MutableComponent tryTranslateCriterion(AdvancementHolder advancementHolder, String criterion) {
        String betterAdvancementsTranslation = "betteradvancements.criterion." + advancementHolder.id() + "." + criterion;
        String preppedCriterion = criterion.replace(":",".");
        String biomeTranslation = "biome." + preppedCriterion;
        String itemTranslation = "item." + preppedCriterion;
        String blockTranslation = "block." + preppedCriterion;
        List<String> translations = List.of(preppedCriterion, betterAdvancementsTranslation, biomeTranslation, itemTranslation, blockTranslation);

        Language language = Language.getInstance();
        for (String translation : translations) {
            if (language.has(translation)) {
                return Component.translatable(translation);
            }
        }

        return Component.translatableWithFallback(preppedCriterion, criterion);
    }
}
