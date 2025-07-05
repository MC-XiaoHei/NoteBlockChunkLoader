package cn.xor7.xiaohei.note_block_chunk_loader.mixin.bridge;

public class EventBridgeManager {
    public static EventBridgeManager INSTANCE = new EventBridgeManager();
    private EventBridge bridge;

    private EventBridgeManager() {}

    public void setBridge(EventBridge bridge) {
        this.bridge = bridge;
    }

    public EventBridge getBridge() {
        return bridge;
    }
}
