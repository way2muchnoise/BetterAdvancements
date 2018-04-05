package betteradvancements.advancements;

import betteradvancements.util.FolderUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
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
        WorldServer world = null;
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && Minecraft.getMinecraft().getIntegratedServer().worlds.length > 0){
            world = (WorldServer) Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
        }
        load(root.getId(), world);
    }

    public BetterDisplayInfo get(Advancement advancement) {
        return registry.getOrDefault(advancement.getId(), new BetterDisplayInfo(advancement));
    }

    private void load(ResourceLocation location, WorldServer world) {
        JsonParser parser = new JsonParser();
        FolderUtil.findAdvancements(location, world, null,
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
                ResourceLocation key = new ResourceLocation(location.getResourceDomain(), name);

                if (!registry.containsKey(key)) {
                    BufferedReader reader = null;

                    try {
                        reader = Files.newBufferedReader(file);
                        JsonObject advancement = parser.parse(reader).getAsJsonObject();
                        JsonObject betterDisplay = advancement.getAsJsonObject("better_display");
                        registry.put(key, new BetterDisplayInfo(key, betterDisplay));
                    } catch (JsonParseException jsonparseexception) {
                        FMLLog.log.error("Parsing error loading built-in advancement " + key, jsonparseexception);
                        return false;
                    } catch (IOException ioexception) {
                        FMLLog.log.error("Couldn't read advancement " + key + " from " + file, ioexception);
                        return false;
                    } finally {
                        IOUtils.closeQuietly(reader);
                    }
                }

                return true;
            }, true, true);
    }
}
