import { CircleCloseFilled, QuestionFilled, WarningFilled } from "@element-plus/icons-vue";

export const lauchercategories = [
  {
    id: 'launcher_failedstart',
    name: '启动器点不开',
    icon: CircleCloseFilled,
    description: '启动器无法打开',
    color: '#E6A23C',
    //docPath: '/docs/launcher_crash.md'
  },
  {
    id: 'launcher_download',
    name: '下载问题',
    icon: WarningFilled,
    description: '游戏文件或资源下载失败',
    color: '#E6A23C',
    //docPath: '/docs/launcher_download.md'
  },
  {
    id: 'launcher_login',
    name: '账号登录问题',
    icon: QuestionFilled,
    description: '无法登录账号或认证失败',
    color: '#E6A23C',
    //docPath: '/docs/launcher_login.md'
  }
]
