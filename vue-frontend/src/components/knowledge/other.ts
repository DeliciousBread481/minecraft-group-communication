import { Folder, WarningFilled } from "@element-plus/icons-vue";

export const otherCategories = [
  {
    id: 'other_performance',
    name: '性能问题',
    icon: WarningFilled,
    description: '游戏游玩卡顿（不仅限于单个存档）',
    color: '#67C23A',
    docPath: '/docs/other_performance.md',
    // 性能问题的文档步骤
    docSteps: [
      {
        title: "问题表现",
        description: "性能问题的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "配置检查",
        description: "检查硬件配置",
        icon: "Monitor",
        docSuffix: "_hardware"
      },
      {
        title: "显卡设置",
        description: "优化显卡设置",
        icon: "VideoPlay",
        docSuffix: "_gpu"
      },
      {
        title: "游戏优化",
        description: "游戏内设置优化",
        icon: "Setting",
        docSuffix: "_settings"
      },
      {
        title: "模组优化",
        description: "模组性能优化",
        icon: "Folder",
        docSuffix: "_mods"
      }
    ]
  },
  {
    id: 'other_saves',
    name: '存档问题',
    icon: Folder,
    description: '存档损坏、存档卡顿排查',
    color: '#67C23A',
    docPath: '/docs/other_saves.md',
    // 存档问题的文档步骤
    docSteps: [
      {
        title: "问题表现",
        description: "存档问题的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "存档损坏",
        description: "存档损坏的分析",
        icon: "WarningFilled",
        docSuffix: "_corruption"
      },
      {
        title: "存档修复",
        description: "存档修复方法",
        icon: "Refresh",
        docSuffix: "_repair"
      },
      {
        title: "存档备份",
        description: "如何备份存档",
        icon: "CopyDocument",
        docSuffix: "_backup"
      },
      {
        title: "存档优化",
        description: "存档性能优化",
        icon: "Tools",
        docSuffix: "_optimize"
      }
    ]
  }
]
