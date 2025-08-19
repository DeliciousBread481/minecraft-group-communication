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
 * 统一处理API错误
 * @param error 错误对象
 * @param defaultMessage 默认错误消息
 * @param showMessage 是否显示错误消息
 */
export const handleApiError = (
  error: ApiError,
  defaultMessage: string = '操作失败，请稍后重试',
  showMessage: boolean = true
): never => {
  let errorMessage = defaultMessage;

  if (error.response?.data?.message) {
    errorMessage = error.response.data.message;
  } else if (error.message) {
    errorMessage = error.message;
  }

  if (showMessage) {
    showError(errorMessage);
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