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