/**
 * 复制文本到剪贴板
 * @param text 要复制的文本
 * @returns 是否复制成功
 */
export const copyToClipboard = (text: string): boolean => {
  try {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    textArea.style.position = 'fixed';
    textArea.style.top = '0';
    textArea.style.left = '0';
    textArea.style.opacity = '0';

    document.body.appendChild(textArea);

    // 选择文本
    textArea.focus();
    textArea.select();

    // 执行复制命令
    const successful = document.execCommand('copy');

    // 清理
    document.body.removeChild(textArea);

    return successful;
  } catch (err) {
    console.error('复制到剪贴板失败:', err);
    return false;
  }
};

/**
 * 异步复制文本到剪贴板
 * @param text 要复制的文本
 * @returns Promise 是否复制成功
 */
export const copyToClipboardAsync = async (text: string): Promise<boolean> => {
  try {
    // 使用现代 Clipboard API
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(text);
      return true;
    }

    // 回退到传统方法
    return copyToClipboard(text);
  } catch (err) {
    console.error('异步复制失败:', err);
    return copyToClipboard(text); // 尝试传统方法
  }
};