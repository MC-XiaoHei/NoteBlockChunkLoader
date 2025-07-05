package cn.xor7.xiaohei.note_block_chunk_loader;

import cn.xor7.xiaohei.note_block_chunk_loader.bridge.EventBridgeImpl;
import cn.xor7.xiaohei.note_block_chunk_loader.mixin.bridge.EventBridgeManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NoteBlockChunkLoader extends JavaPlugin {
    public static NoteBlockChunkLoader plugin = null;
    private PluginConfig pluginConfig;

    @Override
    public void onLoad() {
        plugin = this;
        pluginConfig = readPluginConfig();
        EventBridgeManager.INSTANCE.setBridge(new EventBridgeImpl());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private @NotNull PluginConfig readPluginConfig() {
        saveDefaultConfig();
        PluginConfig result = PluginConfig.fromConfig(getConfig());
        saveConfig();
        return result;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
