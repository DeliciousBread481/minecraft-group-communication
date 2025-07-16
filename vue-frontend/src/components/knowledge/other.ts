import { Folder, WarningFilled } from "@element-plus/icons-vue";

export const otherCategories = [
  {
    id: 'other_performance',
    name: '性能问题',
    icon: WarningFilled,
    description: '游戏游玩卡顿（不仅限于单个存档）',
    color: '#67C23A',
    //docPath: '/docs/other_performance.md'
  },
  {
    id: 'other_saves',
    name: '存档问题',
    icon: Folder,
    description: '存档损坏、存档卡顿排查',
    color: '#67C23A',
    //docPath: '/docs/other_saves.md'
  }
]
