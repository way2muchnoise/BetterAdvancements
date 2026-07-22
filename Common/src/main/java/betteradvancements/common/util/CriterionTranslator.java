package betteradvancements.common.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.LinkedList;
import java.util.List;

public class CriterionTranslator {
    public static MutableComponent tryTranslateCriterion(AdvancementHolder advancementHolder, String criterion) {
        String preppedCriterion = criterion.replace(":",".");
        if (!criterion.contains(":")) preppedCriterion = advancementHolder.id().getNamespace()+ "." + preppedCriterion;
        List<String> translations = new LinkedList<>();
        translations.add(preppedCriterion);
        translations.add("betteradvancements.criterion." + advancementHolder.id() + "." + criterion);
        translations.add("biome." + preppedCriterion);
        translations.add("item." + preppedCriterion);
        translations.add("block." + preppedCriterion);
        translations.add("entity." + preppedCriterion);

        if (criterion.startsWith("armor_trimmed")) {
            String guessType = criterion.split(":")[1].split("_")[0];
            String guessOrigin = criterion.split(":")[0].replace("armor_trimmed","").split("_")[1];
            translations.add("trim_pattern." + guessOrigin + "." + guessType);
            translations.add("trim_material." + guessOrigin + "." + guessType);
        }

        Language language = Language.getInstance();
        for (String translation : translations) {
            if (language.has(translation)) {
                return Component.translatable(translation);
            }
        }

        return Component.translatableWithFallback(preppedCriterion, criterion);
    }
}
