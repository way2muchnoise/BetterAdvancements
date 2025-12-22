package betteradvancements.fabric;

import betteradvancements.common.platform.IAdvancementVisitor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FabricAdvancementVisitor implements IAdvancementVisitor {
    @Override
    public boolean findAdvancements(Identifier location, ServerLevel serverLevel, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor, boolean defaultUnfoundRoot, boolean visitAllFiles) {
        return false;
    }
}
