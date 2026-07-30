// @/utils/message.ts
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';

/**
 * 清洗后端返回的消息，移除敏感信息后再展示给用户
 */
export const sanitizeApiMessage = (
  message?: string | null,
  fallback: string = '操作失败'
): string => {
  if (!message || !message.trim()) {
    return fallback;
  }

  let sanitized = message.trim().replace(/\s+/g, ' ');
  sanitized = sanitized.replace(/\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b/gi, '[REDACTED_EMAIL]');
  sanitized = sanitized.replace(/\bBearer\s+[A-Za-z0-9._-]+/gi, '[REDACTED_BEARER]');
  sanitized = sanitized.replace(/\b(?:https?:\/\/|www\.)\S+/gi, '[REDACTED_URL]');
  sanitized = sanitized.replace(/(?:[A-Za-z]:\\|\/)(?:[^\s,;]+)/g, '[REDACTED_PATH]');
  sanitized = sanitized.replace(/\b(accessToken|refreshToken|token|password|secret|authorization)\b\s*[:=]\s*([^,;\s]+)/gi, '$1: [REDACTED]');

  if (!sanitized.trim()) {
    return fallback;
  }

  if (/\b(?:Caused by|Exception|Error)\b|\bat\s+\S+\(.*\)/i.test(sanitized)) {
    return fallback;
  }

  return sanitized;
};

export const showMessage = (
  message: string,
  type: 'success' | 'warning' | 'info' | 'error' = 'info'
) => {
  ElMessage({
    message: sanitizeApiMessage(message, message),
    type,
    duration: 3000,
    showClose: true,
  });
};

/**
 * 显示通知
 */
export const showNotification = (
  title: string,
  message: string,
  type: 'success' | 'warning' | 'info' | 'error' = 'info'
) => {
  ElNotification({
    title: sanitizeApiMessage(title, title),
    message: sanitizeApiMessage(message, message),
    type,
    duration: 5000,
  });
};

/**
 * 显示确认对话框
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

export interface ApiError extends Error {
  response?: {
    data?: {
      message?: string;
      code?: string | number;
      success?: boolean;
      status?: number;
    };
    status?: number;
  };
}

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
    504: '请求超时，请检查网络连接',
  };

  return statusMessages[status] || defaultMessage;
};

export const handleApiError = (
  error: ApiError,
  defaultMessage: string = '操作失败，请稍后重试',
  showMessage: boolean = true,
  context?: string
): never => {
  let errorMessage = defaultMessage;
  let showAsNotification = false;

  if (error.response) {
    const status = error.response.status ?? 0;
    const serverMessage = sanitizeApiMessage(error.response.data?.message, '');

    if (serverMessage) {
      errorMessage = serverMessage;
    } else {
      errorMessage = getErrorMessageByStatus(status, defaultMessage);
    }

    showAsNotification = [401, 403, 500, 503].includes(status);

    if (status === 401) {
      errorMessage += ' (点击任意页面跳转到登录页)';
    }
  } else if (error.message) {
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

  if (context) {
    errorMessage = `${context}: ${errorMessage}`;
  }

  if (showMessage) {
    showError(errorMessage, '操作失败', showAsNotification);
  }

  throw new Error(errorMessage);
};

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
