package betteradvancements.advancements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class BetterDisplayInfoRegistry {
    private Map<ResourceLocation, BetterDisplayInfo> registry;

    public BetterDisplayInfoRegistry(Advancement root) {
        registry = new HashMap<>();
        load(root.getId());
    }

    public BetterDisplayInfo get(Advancement advancement) {
        return registry.getOrDefault(advancement.getId(), new BetterDisplayInfo(advancement.getId()));
    }

    private void load(ResourceLocation location) {
        ModContainer modContainer = FMLCommonHandler.instance().findContainerFor(location.getResourceDomain());
        JsonParser parser = new JsonParser();
        CraftingHelper.findFiles(modContainer, "assets/" + modContainer.getModId() + "/advancements", null,
            (root, file) ->
            {

                String relative = root.relativize(file).toString();
                if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                    return true;

                String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                ResourceLocation key = new ResourceLocation(modContainer.getModId(), name);

                if (!registry.containsKey(key))
                {
                    BufferedReader reader = null;

                    try
                    {
                        reader = Files.newBufferedReader(file);
                        JsonObject advancement = parser.parse(reader).getAsJsonObject();
                        JsonObject betterDisplay = advancement.getAsJsonObject("better_display");
                        registry.put(key, new BetterDisplayInfo(key, betterDisplay));
                    }
                    catch (JsonParseException jsonparseexception)
                    {
                        FMLLog.log.error("Parsing error loading built-in advancement " + key, jsonparseexception);
                        return false;
                    }
                    catch (IOException ioexception)
                    {
                        FMLLog.log.error("Couldn't read advancement " + key + " from " + file, ioexception);
                        return false;
                    }
                    finally
                    {
                        IOUtils.closeQuietly(reader);
                    }
                }

                return true;
            }, true, true);
    }
}
