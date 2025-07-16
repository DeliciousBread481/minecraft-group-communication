import { ref, type Component } from "vue";
import { CircleCloseFilled, More, Switch, VideoPlay } from "@element-plus/icons-vue";

interface Category {
  id: string;
  name: string;
  icon: Component;
  description: string;
  color: string;
}

export const categories = ref<Category[]>([
  {
    id: 'crash',
    name: '游戏崩溃类',
    icon: CircleCloseFilled,
    description: '游戏启动失败 游戏闪退 游戏崩溃等',
    color: '#9c27b0'
  },
  {
    id: 'disconnect',
    name: '连接失败类',
    icon: Switch,
    description: '联机失败 连接服务器失败 单人存档被踢出等',
    color: '#409EFF'
  },
  {
    id: 'laucher',
    name: '启动器问题',
    icon: VideoPlay,
    description: '启动器无法启动 下载失败 账号无法登录等',
    color: '#E6A23C'
  },
  {
    id: 'other',
    name: '其他问题',
    icon: More,
    description: '其他问题',
    color: '#67C23A'
  }
])
