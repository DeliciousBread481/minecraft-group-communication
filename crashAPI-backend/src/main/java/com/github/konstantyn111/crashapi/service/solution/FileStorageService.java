package com.github.konstantyn111.crashapi.service.solution;

import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件存储服务
 * <p>
 * 提供通用的文件上传和管理功能，支持自定义存储规则、验证条件和目录结构。
 * </p>
 */
@Service
public class FileStorageService {

    // 基础存储目录
    private final String baseUploadDir = "uploads";

    /**
     * 存储多个文件
     * <p>
     * 批量处理文件上传，使用默认配置存储到解决方案目录（solution）。
     * </p>
     *
     * @param files 要存储的文件列表
     * @return 文件访问URL列表
     * @throws RuntimeException 当文件存储失败时抛出
     */
    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String fileName = storeFile(file, "solution", generateUniqueFileName(file), -1, null);
            urls.add(fileName);
        }
        return urls;
    }

    /**
     * 存储单个文件
     * <p>
     * 通用文件存储方法，支持自定义存储参数和验证规则。
     * </p>
     *
     * @param file 上传的文件
     * @param subDirectory 存储子目录（基于baseUploadDir）
     * @param fileName 自定义文件名（含扩展名），null时自动生成
     * @param maxAllowedSize 最大允许字节数（-1表示不限制）
     * @param allowedMimeTypePrefix 允许的MIME类型前缀（如"image/", null表示不验证）
     * @return 存储后的文件名（不含基础URL）
     * @throws BusinessException 当文件验证失败或存储出错时抛出
     */
    public String storeFile(
            MultipartFile file,
            String subDirectory,
            String fileName,
            long maxAllowedSize,
            String allowedMimeTypePrefix
    ) {
        try {
            // 验证基础文件属性
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND,
                        HttpStatus.BAD_REQUEST,
                        "文件不能为空");
            }

            // MIME类型验证
            if (allowedMimeTypePrefix != null) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith(allowedMimeTypePrefix)) {
                    throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE,
                            HttpStatus.BAD_REQUEST,
                            "仅支持" + allowedMimeTypePrefix + "类型文件");
                }
            }

            // 文件大小验证
            if (maxAllowedSize > 0 && file.getSize() > maxAllowedSize) {
                throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED,
                        HttpStatus.BAD_REQUEST,
                        "文件大小不能超过" + (maxAllowedSize / 1024 / 1024) + "MB");
            }

            // 确定最终文件名
            String finalFileName = fileName != null ?
                    fileName : generateUniqueFileName(file);

            // 构建存储路径
            Path storageDir = Paths.get(baseUploadDir, subDirectory);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            // 防止路径穿越攻击
            Path targetPath = storageDir.resolve(finalFileName).normalize();
            if (!targetPath.startsWith(storageDir)) {
                throw new BusinessException(ErrorCode.INVALID_FILE_PATH,
                        HttpStatus.BAD_REQUEST,
                        "非法文件路径");
            }

            // 保存文件
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return finalFileName;
        } catch (BusinessException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "文件存储失败: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "文件处理错误: " + ex.getMessage());
        }
    }

    /**
     * 生成唯一文件名
     * <p>
     * 使用UUID作为文件名主体，保留原始文件扩展名。
     * </p>
     */
    private String generateUniqueFileName(MultipartFile file) {
        return UUID.randomUUID() + "." + getFileExtension(file);
    }

    /**
     * 获取文件扩展名
     * <p>
     * 从原始文件名中提取扩展名，若无扩展名则使用"bin"。
     * </p>
     */
    private String getFileExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "bin";
    }
}