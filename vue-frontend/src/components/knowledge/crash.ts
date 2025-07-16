import { CircleCloseFilled, Close, WarningFilled } from '@element-plus/icons-vue'

export const crashCategories = [
  {
    id: 'crash_startup',
    name: '启动失败',
    icon: Close,
    description: '游戏无法启动或启动过程中崩溃',
    color: '#9c27b0',
    docPath: '/docs/crash_startup.md'
  },
  {
    id: 'crash_suddenly',
    name: '游戏闪退',
    icon: WarningFilled,
    description: '游戏游玩中突然崩溃且窗口突然消失',
    color: '#9c27b0',
    docPath: '/docs/crash_mod.md'
  },
  {
    id: 'crash_ingame',
    name: '游戏内崩溃',
    icon: CircleCloseFilled,
    description: '游戏运行中突然崩溃',
    color: '#9c27b0',
    docPath: '/docs/crash_ingame.md'
  }
]
