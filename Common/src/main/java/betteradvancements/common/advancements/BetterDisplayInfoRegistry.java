package betteradvancements.common.advancements;

import betteradvancements.common.platform.Services;
import betteradvancements.common.reference.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class BetterDisplayInfoRegistry {
    private final Map<ResourceLocation, BetterDisplayInfo> registry;

    public BetterDisplayInfoRegistry(Advancement advancement) {
        registry = new HashMap<>();
    }

    public BetterDisplayInfo get(Advancement advancement) {
        return registry.getOrDefault(advancement.getId(), new BetterDisplayInfo(advancement));
    }

    private void load(ResourceLocation location, ServerLevel serverLevel) {
        Services.PLATFORM.getAdvancementVisitor().findAdvancements(location, serverLevel, null,
            (root, file) ->
            {
                String relative;
                try {
                    relative = root.relativize(file).toString();
                } catch (Exception e) {
                    relative = "";
                }
                if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                    return true;

                String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                ResourceLocation key = new ResourceLocation(location.getNamespace(), name);

                if (!registry.containsKey(key)) {
                    BufferedReader reader = null;

                    try {
                        reader = Files.newBufferedReader(file);
                        JsonObject advancement = JsonParser.parseReader(reader).getAsJsonObject();
                        JsonObject betterDisplay = advancement.getAsJsonObject("better_display");
                        registry.put(key, new BetterDisplayInfo(key, betterDisplay));
                    } catch (JsonParseException jsonparseexception) {
                        Constants.log.error("Parsing error loading built-in advancement " + key, jsonparseexception);
                        return false;
                    } catch (IOException ioexception) {
                        Constants.log.error("Couldn't read advancement " + key + " from " + file, ioexception);
                        return false;
                    } finally {
                        IOUtils.closeQuietly(reader);
                    }
                }

                return true;
            }, true, true);
    }
}
