package com.github.konstantyn111.crashapi.util.admin;

import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;

import java.time.LocalDateTime;
import java.util.List;

public class SolutionUpdateUtil {

    /**
     * 添加解决方案的步骤
     *
     * @param solutionId 解决方案ID
     * @param steps 步骤内容列表
     * @param solutionStepMapper 解决方案步骤Mapper
     */
    private static void addSolutionSteps(
            String solutionId,
            List<String> steps,
            SolutionStepMapper solutionStepMapper) {

        if (steps != null && !steps.isEmpty()) {
            for (int i = 0; i < steps.size(); i++) {
                SolutionStep step = new SolutionStep();
                step.setSolutionId(solutionId);
                step.setStepOrder(i + 1);
                step.setContent(steps.get(i));
                solutionStepMapper.insert(step);
            }
        }
    }

    /**
     * 添加解决方案的图片
     *
     * @param solutionId 解决方案ID
     * @param imageUrls 图片URL列表
     * @param solutionImageMapper 解决方案图片Mapper
     */
    private static void addSolutionImages(
            String solutionId,
            List<String> imageUrls,
            SolutionImageMapper solutionImageMapper) {

        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (int i = 0; i < imageUrls.size(); i++) {
                SolutionImage image = new SolutionImage();
                image.setSolutionId(solutionId);
                image.setImageOrder(i + 1);
                image.setImageUrl(imageUrls.get(i));
                solutionImageMapper.insert(image);
            }
        }
    }

    /**
     * 创建解决方案的步骤和图片
     *
     * @param solution 解决方案实体
     * @param createDTO 创建数据
     * @param solutionStepMapper 解决方案步骤Mapper
     * @param solutionImageMapper 解决方案图片Mapper
     */
    public static void createSolutionStepsAndImages(
            Solution solution,
            SolutionCreateDTO createDTO,
            SolutionStepMapper solutionStepMapper,
            SolutionImageMapper solutionImageMapper) {

        addSolutionSteps(solution.getId(), createDTO.getSteps(), solutionStepMapper);
        addSolutionImages(solution.getId(), createDTO.getImageUrls(), solutionImageMapper);
    }

    /**
     * 更新解决方案
     *
     * @param solution 要更新的解决方案实体
     * @param updateDTO 更新数据
     * @param solutionMapper 解决方案Mapper
     * @param solutionStepMapper 解决方案步骤Mapper
     * @param solutionImageMapper 解决方案图片Mapper
     */
    public static void updateSolutionCore(
            Solution solution,
            SolutionUpdateDTO updateDTO,
            SolutionMapper solutionMapper,
            SolutionStepMapper solutionStepMapper,
            SolutionImageMapper solutionImageMapper) {

        // 更新解决方案基本信息
        solution.setTitle(updateDTO.getTitle());
        solution.setDifficulty(updateDTO.getDifficulty());
        solution.setVersion(updateDTO.getVersion());
        solution.setDescription(updateDTO.getDescription());
        solution.setNotes(updateDTO.getNotes());
        solution.setUpdatedAt(LocalDateTime.now());

        solutionMapper.update(solution);

        // 删除现有步骤和图片
        solutionStepMapper.deleteBySolutionId(solution.getId());
        solutionImageMapper.deleteBySolutionId(solution.getId());

        // 添加新步骤和图片
        addSolutionSteps(solution.getId(), updateDTO.getSteps(), solutionStepMapper);
        addSolutionImages(solution.getId(), updateDTO.getImageUrls(), solutionImageMapper);
    }
}