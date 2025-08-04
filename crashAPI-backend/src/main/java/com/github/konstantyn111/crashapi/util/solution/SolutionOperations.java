package com.github.konstantyn111.crashapi.util.solution;

import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;
import com.github.konstantyn111.crashapi.service.solution.FileStorageService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 解决方案操作工具类
 * <p>
 * 提供解决方案创建、更新和删除过程中的核心操作逻辑。
 * </p>
 */
@Component
public class SolutionOperations {

    private FileStorageService fileStorageService;

    /**
     * 为解决方案添加步骤
     * <p>
     * 将步骤内容按顺序插入数据库，自动生成步骤序号。
     * </p>
     *
     * @param solutionId 解决方案ID
     * @param steps 步骤内容列表（可为空）
     * @param mapper 解决方案步骤数据访问接口
     */
    public void addSteps(String solutionId, List<String> steps, SolutionStepMapper mapper) {
        if (steps == null || steps.isEmpty()) return;

        for (int i = 0; i < steps.size(); i++) {
            SolutionStep step = new SolutionStep();
            step.setSolutionId(solutionId);
            step.setStepOrder(i + 1);
            step.setContent(steps.get(i));
            mapper.insert(step);
        }
    }

    /**
     * 为解决方案添加图片
     * <p>
     * 将图片URL按顺序插入数据库，自动生成图片序号。
     * </p>
     *
     * @param solutionId 解决方案ID
     * @param imageUrls 图片URL列表（可为空）
     * @param mapper 解决方案图片数据访问接口
     */
    public void addImages(String solutionId, List<String> imageUrls, SolutionImageMapper mapper) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (int i = 0; i < imageUrls.size(); i++) {
            SolutionImage image = new SolutionImage();
            image.setSolutionId(solutionId);
            image.setImageOrder(i + 1);
            image.setImageUrl(imageUrls.get(i));
            mapper.insert(image);
        }
    }

    /**
     * 创建解决方案的步骤和图片关联数据
     * <p>
     * 存储上传的图片文件并添加步骤和图片记录。
     * </p>
     *
     * @param solution 解决方案实体
     * @param createDTO 解决方案创建数据传输对象
     * @param stepMapper 解决方案步骤数据访问接口
     * @param imageMapper 解决方案图片数据访问接口
     */
    public void createStepsAndImages(Solution solution, SolutionCreateDTO createDTO,
                                     SolutionStepMapper stepMapper, SolutionImageMapper imageMapper) {
        addSteps(solution.getId(), createDTO.getSteps(), stepMapper);
        List<String> imageUrls = fileStorageService.storeFiles(createDTO.getImageFiles());
        addImages(solution.getId(), imageUrls, imageMapper);
    }

    /**
     * 更新解决方案核心数据
     * <p>
     * 处理解决方案基本信息和关联数据更新，包括：
     * 1. 合并现有图片URL和新上传图片
     * 2. 更新解决方案基本信息
     * 3. 删除原有步骤和图片记录
     * 4. 添加新步骤和图片记录
     * </p>
     *
     * @param solution 解决方案实体
     * @param updateDTO 解决方案更新数据传输对象
     * @param solutionMapper 解决方案数据访问接口
     * @param stepMapper 解决方案步骤数据访问接口
     * @param imageMapper 解决方案图片数据访问接口
     */
    public void updateCore(Solution solution, SolutionUpdateDTO updateDTO,
                           SolutionMapper solutionMapper, SolutionStepMapper stepMapper,
                           SolutionImageMapper imageMapper) {

        List<String> imageUrls = updateDTO.getExistingImageUrls() != null ?
                new ArrayList<>(updateDTO.getExistingImageUrls()) : new ArrayList<>();

        imageUrls.addAll(fileStorageService.storeFiles(updateDTO.getImageFiles()));

        solution.setTitle(updateDTO.getTitle());
        solution.setDifficulty(updateDTO.getDifficulty());
        solution.setVersion(updateDTO.getVersion());
        solution.setDescription(updateDTO.getDescription());
        solution.setNotes(updateDTO.getNotes());
        solution.setUpdatedAt(LocalDateTime.now());
        solutionMapper.update(solution);

        stepMapper.deleteBySolutionId(solution.getId());
        imageMapper.deleteBySolutionId(solution.getId());

        addSteps(solution.getId(), updateDTO.getSteps(), stepMapper);
        addImages(solution.getId(), imageUrls, imageMapper);
    }

    /**
     * 删除解决方案关联数据
     * <p>
     * 清除解决方案的所有步骤和图片记录。
     * </p>
     *
     * @param solutionId 解决方案ID
     * @param stepMapper 解决方案步骤数据访问接口
     * @param imageMapper 解决方案图片数据访问接口
     */
    public void deleteAssociations(String solutionId,
                                   SolutionStepMapper stepMapper,
                                   SolutionImageMapper imageMapper) {
        stepMapper.deleteBySolutionId(solutionId);
        imageMapper.deleteBySolutionId(solutionId);
    }
}