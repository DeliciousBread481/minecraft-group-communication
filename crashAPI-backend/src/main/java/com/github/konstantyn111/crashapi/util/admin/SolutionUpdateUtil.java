package com.github.konstantyn111.crashapi.util.admin;

import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;
import com.github.konstantyn111.crashapi.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SolutionUpdateUtil {
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 添加解决方案的步骤
     */
    public void addSolutionSteps(
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
     */
    public void addSolutionImages(
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
     */
    public void createSolutionStepsAndImages(
            Solution solution,
            SolutionCreateDTO createDTO,
            SolutionStepMapper solutionStepMapper,
            SolutionImageMapper solutionImageMapper
    ) {
        addSolutionSteps(solution.getId(), createDTO.getSteps(), solutionStepMapper);
        List<String> imageUrls = fileStorageService.storeFiles(createDTO.getImageFiles());
        addSolutionImages(solution.getId(), imageUrls, solutionImageMapper);
    }

    /**
     * 更新解决方案
     */
    public void updateSolutionCore(
            Solution solution,
            SolutionUpdateDTO updateDTO,
            SolutionMapper solutionMapper,
            SolutionStepMapper solutionStepMapper,
            SolutionImageMapper solutionImageMapper) {

        // 直接使用现有图片URL列表
        List<String> imageUrls = updateDTO.getExistingImageUrls() != null ?
                new ArrayList<>(updateDTO.getExistingImageUrls()) : new ArrayList<>();

        // 仅存储新上传的文件
        imageUrls.addAll(fileStorageService.storeFiles(updateDTO.getImageFiles()));

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
        addSolutionImages(solution.getId(), imageUrls, solutionImageMapper);
    }
}