# NoteBlockChunkLoader

[![许可证](https://img.shields.io/github/license/MC-XiaoHei/NoteBlockChunkLoader)](LICENSE)

添加了类似 Carpet AMS Addition 的音符盒加载器

## 特性

- 当上边沿红石信号激活音符盒时为所在区块添加类型为`ENDER_PEARL`，时长为 300tick 的加载票
- 可配置的触发条件（音符盒顶部方块类型，底部方块类型）
- 可配置的加载范围

## 注意事项

本插件依赖 Mixin 功能，你需要在服务器启动脚本中加入 `-Dleavesclip.enable.mixin=true`

## 配置文件

```yaml
# 加载范围
load-radius: 3

# 由于在服务器当前维度没有玩家的 300tick 后，Minecraft 会停止实体相关的更新，
# 因此每当当前维度中没有玩家时使用该规则加载的区块中漏斗将会停止工作，
# 将该配置项设置为 true 可以避免这种情况。
keep-world-tick-update: false

# 上升沿红石信号激活音符盒时，若音符盒顶部方块不为此方块，则不加载所在区块
load-when-above-block-is: wither_skeleton_skull

# 下降沿红石信号激活音符盒时，若音符盒底部方块不为此方块，则不加载所在区块
load-when-below-block-is: any
```

## 常见问题

**此插件可以用于哪些服务端？**

目前只有 [Leaves](https://github.com/LeavesMC/Leaves)

**为什么插件不起作用？**

见注意事项，确保在服务器启动脚本中加入了 `-Dleavesclip.enable.mixin=true`。

## 贡献

欢迎通过 Issue 或 Pull Request 提交你的建议或改进！

## 许可证

本项目遵循 [MIT License](LICENSE) 开源协议。