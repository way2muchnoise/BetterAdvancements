package betteradvancements.neoforge;

import betteradvancements.common.platform.IAdvancementVisitor;
import betteradvancements.common.reference.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NeoForgeAdvancementVisitor implements IAdvancementVisitor {
    public boolean findAdvancements(ResourceLocation location, ServerLevel serverLevel, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor, boolean defaultUnfoundRoot, boolean visitAllFiles) {
        FileSystem fs = null;
        ModContainer mod = ModList.get().getModContainerById(location.getNamespace()).orElse(null);
        try {
            File source = null;

            if (mod != null) {
                // source = mod.getSource();

                if ("minecraft".equals(mod.getModId())) {
                    try
                    {
                        URI tmp = RecipeManager.class.getResource("/assets/.mcassetsroot").toURI();
                        source = new File(tmp.resolve("..").getPath());
                    }
                    catch (URISyntaxException e)
                    {
                        Constants.log.error("Error finding Minecraft jar: ", e);
                        return false;
                    }
                }
            }

            Path root = null;
            if (source == null) {
                if (serverLevel != null) {
                    root = serverLevel.getServer().getServerDirectory().toPath().resolve("advancements/" + location.getNamespace());
                }
            }
            else if (source.isFile()) {
                try {
                    fs = FileSystems.newFileSystem(source.toURI(), null);
                    root = fs.getPath("/assets/" + mod.getModId() + "/advancements");
                } catch (IOException e) {
                    Constants.log.error("Error loading FileSystem from jar: ", e);
                    return false;
                }
            } else if (source.isDirectory()) {
                root = source.toPath().resolve("assets/" + mod.getModId() + "/advancements");
            }

            if (root == null || !Files.exists(root))
                return defaultUnfoundRoot;

            if (preprocessor != null) {
                Boolean cont = preprocessor.apply(root);
                if (cont == null || !cont)
                    return false;
            }

            boolean success = true;

            if (processor != null) {
                Iterator<Path> itr = null;
                try {
                    itr = Files.walk(root).iterator();
                } catch (IOException e) {
                    Constants.log.error("Error iterating filesystem for: {}", mod.getModId(), e);
                    return false;
                }

                while (itr != null && itr.hasNext()) {
                    Boolean cont = processor.apply(root, itr.next());

                    if (visitAllFiles) {
                        success &= cont != null && cont;
                    } else if (cont == null || !cont) {
                        return false;
                    }
                }
            }

            return success;
        } finally {
            IOUtils.closeQuietly(fs);
        }
    }
}
