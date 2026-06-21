import { VideoPause } from '@element-plus/icons-vue'
import type { DocStep, LauncherDoc } from './crash'

export interface FreezeCategory {
  id: string
  name: string
  icon: typeof VideoPause
  description: string
  color: string
  launcherDocs: {
    pcl2: LauncherDoc
    hmcl: LauncherDoc
    server: LauncherDoc
  }
}

export const freezeCategory: FreezeCategory = {
  id: 'freeze',
  name: '游戏卡死',
  icon: VideoPause,
  description: '游戏无响应、画面冻结、无法操作',
  color: '#909399',
  launcherDocs: {
    pcl2: {
      docSteps: [
        { title: '问题说明', description: '游戏卡死的表现与排查', icon: 'Document', jsonPath: '/docs/freeze_pcl2.json' },
        { title: '补充信息', description: '补充更多信息', icon: 'InfoFilled', jsonPath: '/docs/crash_info.json' }
      ]
    },
    hmcl: {
      docSteps: [
        { title: '问题说明', description: '游戏卡死的表现与排查', icon: 'Document', jsonPath: '/docs/freeze_hmcl.json' },
        { title: '补充信息', description: '补充更多信息', icon: 'InfoFilled', jsonPath: '/docs/crash_info.json' }
      ]
    },
    server: {
      docSteps: [
        { title: '问题说明', description: '服务端/控制台卡死的表现与排查', icon: 'Document', jsonPath: '/docs/freeze_server.json' },
        { title: '补充信息', description: '补充更多信息', icon: 'InfoFilled', jsonPath: '/docs/crash_info.json' }
      ]
    }
  }
}
