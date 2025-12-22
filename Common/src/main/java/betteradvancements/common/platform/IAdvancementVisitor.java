package betteradvancements.common.platform;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IAdvancementVisitor {
    boolean findAdvancements(Identifier location, ServerLevel serverLevel, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor, boolean defaultUnfoundRoot, boolean visitAllFiles);
}
