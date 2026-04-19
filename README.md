# Minecraft Crash Solutions Platform

Minecraft 崩溃解决方案管理平台 —— 提供崩溃问题的创建、审核与发布的完整工作流。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus + Pinia |
| 后端 | Spring Boot 3.5 + Java 17 + MyBatis + Spring Security |
| 数据库 | MySQL 8 |
| 认证 | JWT（Access Token + Refresh Token） |

## 项目结构

```
├── crashAPI-backend/                # Spring Boot 后端（端口 9090）  
│   ├── src/main/java/.../crashapi/  
│   │   ├── controller/              # 接口层（Auth/Admin/Developer/User）  
│   │   ├── service/                 # 业务逻辑层  
│   │   ├── mapper/                  # MyBatis Mapper 接口  
│   │   ├── entity/                  # 数据实体类  
│   │   ├── dto/                     # 数据传输对象  
│   │   ├── config/                  # Security / JWT / CORS 配置  
│   │   └── utils/                   # 工具类  
│   ├── src/main/resources/  
│   │   ├── mapper/                  # MyBatis XML 映射文件  
│   │   └── application.properties   # 应用配置  
│   ├── crash_api.sql                # 数据库初始化脚本  
│   └── pom.xml  
│  
└── vue-frontend/                    # Vue 3 前端（端口 3000）  
    └── src/  
        ├── api/                     # 接口请求（auth / admin / developer / user）  
        ├── components/              # 公共组件（layout / knowledge）  
        ├── views/                   # 页面视图  
        │   ├── AuthView.vue         #   登录 / 注册  
        │   ├── HomeView.vue         #   首页  
        │   ├── SolutionView.vue     #   解决方案详情  
        │   ├── AdminView.vue        #   管理员面板  
        │   ├── NoticeView.vue       #   公告页  
        │   └── SettingsView.vue     #   用户设置  
        ├── store/                   # Pinia 状态管理（auth / admin / developer / theme / user）  
        ├── router/                  # 路由配置  
        ├── types/                   # TypeScript 类型定义  
        ├── utils/                   # 工具函数（API 封装 / 剪贴板 / 主题）  
        ├── styles/                  # 全局样式  
        ├── App.vue  
        └── main.ts
```

## 角色与权限

| 角色 | 说明 |
|------|------|
| `ROLE_USER` | 默认角色，可浏览已发布的解决方案 |
| `ROLE_ADMIN` | 内容管理员，可创建/编辑/提交解决方案 |
| `ROLE_DEV` | 超级管理员，可审核解决方案、管理用户角色 |

## 解决方案生命周期

```
DRAFT → PENDING_REVIEW → PUBLISHED
  ↑          |
  └──────────┘ (驳回)
```

## 快速开始

### 1. 数据库初始化

```bash
mysql -u root -p < crashAPI-backend/crash_api.sql
```

### 2. 启动后端

```bash
cd crashAPI-backend
# 修改 src/main/resources/application.properties 中的数据库连接信息
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd vue-frontend
npm install
npm run dev
```

访问 `http://localhost:3000` 即可使用。

## API 概览

| 模块 | 路径前缀 | 权限 |
|------|----------|------|
| 认证 | `/crashapi/api/auth/**` | 公开 |
| 解决方案（公开） | `/crashapi/api/solutions/**` | 所有用户 |
| 用户 | `/crashapi/api/user/**` | `ROLE_USER+` |
| 管理员 | `/crashapi/api/admin/**` | `ROLE_ADMIN` |
| 开发者 | `/crashapi/api/developer/**` | `ROLE_DEV` |
