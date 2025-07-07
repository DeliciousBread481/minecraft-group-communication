import axios from 'axios'

/**
 * 创建并配置Axios实例
 *
 * 该实例设置了基础URL、超时时间和默认请求头。
 * 添加了请求拦截器用于自动添加JWT令牌，
 * 以及响应拦截器用于统一处理响应和错误。
 */
const api = axios.create({
  /**
   * API基础URL，优先使用环境变量VITE_API_BASE_URL的值，
   * 若未设置则默认使用'http://api'
   */
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://api',

  /** 请求超时时间（毫秒） */
  timeout: 10000,

  /** 默认请求头配置 */
  headers: {
    'Content-Type': 'application/json'  // 设置请求体为JSON格式
  }
})

/**
 * 请求拦截器
 *
 * 在每次请求发送前执行，用于注入身份验证令牌。
 * 如果本地存储中存在'token'，则自动添加到请求头的Authorization字段。
 */
api.interceptors.request.use(
  config => {
    // 从本地存储获取JWT令牌
    const token = localStorage.getItem('token')

    // 若令牌存在，将其添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 请求配置错误时直接拒绝Promise
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 *
 * 处理所有API响应：
 * - 成功响应：直接返回response.data（剥离axios包装）
 * - 错误响应：打印错误日志并传递错误
 */
api.interceptors.response.use(
  response => {
    // 成功响应时直接返回响应数据主体
    return response.data
  },
  error => {
    // 统一处理API错误响应
    console.error('API Error:', error.response)

    // 将错误继续传递给调用方
    return Promise.reject(error)
  }
)

// 导出配置好的Axios实例
export default api
