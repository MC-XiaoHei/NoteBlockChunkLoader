package cn.xor7.xiaohei.note_block_chunk_loader;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public record PluginConfig(
        int loadRadius,
        boolean keepWorldTickUpdate,
        @Nullable Material loadWhenAboveBlockIs,
        @Nullable Material loadWhenBelowBlockIs
) {
    public static final String ANY_BLOCK = "any";
    private static final int DEFAULT_LOAD_RADIUS = 3;
    private static final boolean DEFAULT_KEEP_WORLD_TICK_UPDATE = false;
    private static final String DEFAULT_LOAD_WHEN_ABOVE_BLOCK_IS = "wither_skeleton_skull";
    private static final String DEFAULT_LOAD_WHEN_BELOW_BLOCK_IS = ANY_BLOCK;

    @Contract("_ -> new")
    public static @NotNull PluginConfig fromConfig(@NotNull FileConfiguration config) {
        Logger logger = NoteBlockChunkLoader.plugin.getLogger();

        int loadRadius = config.getInt("load-radius", DEFAULT_LOAD_RADIUS);
        if (loadRadius <= 0) {
            logger.warning("Invalid load-radius in config, using default: " + DEFAULT_LOAD_RADIUS);
            loadRadius = DEFAULT_LOAD_RADIUS;
        }
        config.set("load-radius", loadRadius);

        boolean keepWorldTickUpdate = config.getBoolean("keep-world-tick-update", DEFAULT_KEEP_WORLD_TICK_UPDATE);
        config.set("keep-world-tick-update", keepWorldTickUpdate);

        String loadWhenAboveBlockIsStr = config.getString("load-when-above-block-is", DEFAULT_LOAD_WHEN_ABOVE_BLOCK_IS);
        Material loadWhenAboveBlockIs = Material.matchMaterial(loadWhenAboveBlockIsStr);
        if (loadWhenAboveBlockIs == null && !loadWhenAboveBlockIsStr.equals(ANY_BLOCK)) {
            logger.warning("Invalid load-when-above-block-is in config, using default: " + DEFAULT_LOAD_WHEN_ABOVE_BLOCK_IS);
            loadWhenAboveBlockIsStr = DEFAULT_LOAD_WHEN_ABOVE_BLOCK_IS;
            loadWhenAboveBlockIs = Material.matchMaterial(DEFAULT_LOAD_WHEN_ABOVE_BLOCK_IS);
        }
        config.set("load-when-above-block-is", loadWhenAboveBlockIsStr);

        String loadWhenBelowBlockIsStr = config.getString("load-when-below-block-is", DEFAULT_LOAD_WHEN_BELOW_BLOCK_IS);
        Material loadWhenBelowBlockIs = Material.matchMaterial(loadWhenBelowBlockIsStr);
        if (loadWhenBelowBlockIs == null && !loadWhenBelowBlockIsStr.equals(ANY_BLOCK)) {
            logger.warning("Invalid load-when-below-block-is in config, using default: " + DEFAULT_LOAD_WHEN_BELOW_BLOCK_IS);
            loadWhenBelowBlockIsStr = DEFAULT_LOAD_WHEN_BELOW_BLOCK_IS;
            loadWhenBelowBlockIs = Material.matchMaterial(DEFAULT_LOAD_WHEN_BELOW_BLOCK_IS);
        }
        config.set("load-when-below-block-is", loadWhenBelowBlockIsStr);

        if (loadWhenAboveBlockIs == null && loadWhenBelowBlockIs == null) {
            logger.warning("Both load-when-above-block-is and load-when-below-block-is are set to any, which is not recommended.");
        }

        return new PluginConfig(
                loadRadius,
                keepWorldTickUpdate,
                loadWhenAboveBlockIs,
                loadWhenBelowBlockIs
        );
    }
}
