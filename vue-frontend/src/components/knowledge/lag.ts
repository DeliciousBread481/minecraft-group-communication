import { Odometer, Timer } from '@element-plus/icons-vue'
import type { DocStep } from './crash'

interface LagCategory {
  id: string
  name: string
  icon: typeof Odometer
  description: string
  color: string
  docSteps: DocStep[]
}

export const lagCategories: LagCategory[] = [
  {
    id: 'lag_fps',
    name: 'FPS低',
    icon: Odometer,
    description: '画面帧率低、操作不跟手',
    color: '#f56c6c',
    docSteps: [
      { title: '问题说明', description: 'FPS 低的表现与常见原因', icon: 'Document', jsonPath: '/docs/lag_fps_low.json' },
      { title: '补充信息', description: '补充更多信息', icon: 'InfoFilled', jsonPath: '/docs/crash_info.json' }
    ]
  },
  {
    id: 'lag_tps',
    name: 'TPS低',
    icon: Timer,
    description: '游戏逻辑卡顿、方块放置延迟',
    color: '#f56c6c',
    docSteps: [
      { title: '问题说明', description: 'TPS 低的表现与常见原因', icon: 'Document', jsonPath: '/docs/lag_tps_low.json' },
      { title: '补充信息', description: '补充更多信息', icon: 'InfoFilled', jsonPath: '/docs/crash_info.json' }
    ]
  }
]
