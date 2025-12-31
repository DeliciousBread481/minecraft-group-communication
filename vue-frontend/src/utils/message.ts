// @/utils/message.ts
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';

/**
 * 显示消息提示
 * @param message 消息内容
 * @param type 消息类型，可选：'success' | 'warning' | 'info' | 'error'
 */
export const showMessage = (
  message: string,
  type: 'success' | 'warning' | 'info' | 'error' = 'info'
) => {
  ElMessage({
    message,
    type,
    duration: 3000,
    showClose: true,
  });
};

/**
 * 显示通知
 * @param title 通知标题
 * @param message 通知内容
 * @param type 通知类型
 */
export const showNotification = (
  title: string,
  message: string,
  type: 'success' | 'warning' | 'info' | 'error' = 'info'
) => {
  ElNotification({
    title,
    message,
    type,
    duration: 5000,
  });
};

/**
 * 显示确认对话框
 * @param message 消息内容
 * @param title 标题（可选）
 * @returns Promise<boolean> 用户是否确认
 */
export const showConfirm = (
  message: string,
  title: string = '提示'
): Promise<boolean> => {
  return ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => true)
    .catch(() => false);
};

/**
 * 显示提示输入框
 * @param message 提示信息
 * @param title 标题（可选）
 * @returns Promise<string | null> 用户输入的字符串或 null
 */
export const showPrompt = (
  message: string,
  title: string = '请输入'
): Promise<string | null> => {
  return ElMessageBox.prompt(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
    .then(({ value }) => value as string)
    .catch(() => null);
};

/**
 * 错误类型定义
 */
export interface ApiError extends Error {
  response?: {
    data?: {
      message?: string;
      code?: string | number;
    };
    status?: number;
  };
}

/**
 * 根据HTTP状态码获取用户友好的错误消息
 * @param status HTTP状态码
 * @param defaultMessage 默认消息
 */
const getErrorMessageByStatus = (status: number, defaultMessage: string): string => {
  const statusMessages: Record<number, string> = {
    400: '请求参数错误，请检查输入信息',
    401: '您的身份已过期，请重新登录',
    403: '您没有权限执行此操作',
    404: '请求的资源不存在',
    409: '数据冲突，请刷新页面后重试',
    422: '输入数据验证失败，请检查表单内容',
    429: '请求过于频繁，请稍后再试',
    500: '服务器内部错误，请联系管理员',
    502: '网关错误，请稍后重试',
    503: '服务暂时不可用，请稍后重试',
    504: '请求超时，请检查网络连接'
  };
  
  return statusMessages[status] || defaultMessage;
};

/**
 * 统一处理API错误，提供更友好的错误提示
 * @param error 错误对象
 * @param defaultMessage 默认错误消息
 * @param showMessage 是否显示错误消息
 * @param context 错误发生的上下文，用于提供更具体的提示
 */
export const handleApiError = (
  error: ApiError,
  defaultMessage: string = '操作失败，请稍后重试',
  showMessage: boolean = true,
  context?: string
): never => {
  let errorMessage = defaultMessage;
  let showAsNotification = false;

  // 根据不同的错误类型提供具体的错误信息
  if (error.response) {
    const status = error.response.status ?? 0; // 使用默认值避免 undefined
    const serverMessage = error.response.data?.message;
    
    // 如果服务器提供了具体的错误消息，优先使用
    if (serverMessage && !serverMessage.includes('Exception') && !serverMessage.includes('Error')) {
      errorMessage = serverMessage;
    } else {
      // 根据状态码提供友好的错误消息
      errorMessage = getErrorMessageByStatus(status, defaultMessage);
    }
    
    // 对于某些错误类型，使用通知形式显示
    showAsNotification = [401, 403, 500, 503].includes(status);
    
    // 为401错误提供额外的操作建议
    if (status === 401) {
      errorMessage += ' (点击任意页面跳转到登录页)';
    }
    
  } else if (error.message) {
    // 网络错误或其他客户端错误
    if (error.message.includes('Network Error') || error.message.includes('ERR_NETWORK')) {
      errorMessage = '网络连接失败，请检查网络设置';
      showAsNotification = true;
    } else if (error.message.includes('timeout')) {
      errorMessage = '请求超时，请检查网络连接后重试';
      showAsNotification = true;
    } else {
      errorMessage = error.message;
    }
  }
  
  // 如果提供了上下文，在错误消息中包含上下文信息
  if (context) {
    errorMessage = `${context}: ${errorMessage}`;
  }

  if (showMessage) {
    showError(errorMessage, '操作失败', showAsNotification);
  }

  throw new Error(errorMessage);
};

/**
 * 显示错误消息
 * @param message 错误消息内容
 * @param title 错误标题（用于通知）
 * @param useNotification 是否使用通知形式而非消息
 */
export const showError = (
  message: string,
  title: string = '错误',
  useNotification: boolean = false
) => {
  if (useNotification) {
    showNotification(title, message, 'error');
  } else {
    showMessage(message, 'error');
  }
};

/**
 * 显示成功消息
 * @param message 成功消息内容
 * @param title 成功标题（用于通知）
 * @param useNotification 是否使用通知形式而非消息
 */
export const showSuccess = (
  message: string,
  title: string = '成功',
  useNotification: boolean = false
) => {
  if (useNotification) {
    showNotification(title, message, 'success');
  } else {
    showMessage(message, 'success');
  }
};

/**
 * 安全执行异步操作，自动捕获并显示错误
 * @param operation 异步操作函数
 * @param errorMessage 自定义错误消息
 * @param showError 是否显示错误
 * @returns 操作结果
 */
export const safeExecute = async <T>(
  operation: () => Promise<T>,
  errorMessage: string = '操作失败',
  showError: boolean = true
): Promise<T | null> => {
  try {
    return await operation();
  } catch (error) {
    if (showError) {
      handleApiError(error as ApiError, errorMessage, true);
    } else {
      console.error(error);
    }
    return null;
  }
};