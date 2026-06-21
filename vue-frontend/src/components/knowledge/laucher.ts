import { CircleCloseFilled, QuestionFilled } from "@element-plus/icons-vue";
import type { DocStep, LauncherDoc } from "./crash";

export interface LauncherCategory {
  id: string
  name: string
  icon: typeof CircleCloseFilled
  description: string
  color: string
  launcherDocs: {
    pcl2: LauncherDoc
    hmcl: LauncherDoc
  }
}

export const launcherCategories: LauncherCategory[] = [
  {
    id: 'laucher',
    name: '启动器问题',
    icon: QuestionFilled,
    description: '与启动器相关的问题，如无法启动、闪退等',
    color: '#67C23A',
    launcherDocs: {
      pcl2: {
        docSteps: [
          { title: '问题说明', description: '启动器问题的表现与排查', icon: 'Document', jsonPath: '/docs/pcl_launcher.json' },
          { title: '补充信息', description: '补充更多信息', icon: 'InfoFilled', jsonPath: '/docs/crash_info.json' }
        ]
      },
      hmcl: {
        docSteps: [
          { title: '问题说明', description: '启动器问题的表现与排查', icon: 'Document', jsonPath: '/docs/hmcl_launcher.json' },
          { title: '补充信息', description: '补充更多信息', icon: 'InfoFilled', jsonPath: '/docs/crash_info.json' }
        ]
      }
    }
  }
]
