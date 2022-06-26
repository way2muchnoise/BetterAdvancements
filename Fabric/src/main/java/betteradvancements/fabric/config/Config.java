package betteradvancements.fabric.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class Config {
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(new TranslatableComponent("title.betteradvancements.config"));

        builder.setSavingRunnable(ConfigFileHandler::writeToConfig);

        ConfigCategory mainCategory = builder.getOrCreateCategory(new TranslatableComponent("category.betteradvancements.general"));
        ConfigValues.build(mainCategory, builder.entryBuilder());

        return builder.build();
    }
}
