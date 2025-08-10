package com.github.konstantyn111.crashapi.service.solution;

import com.github.konstantyn111.crashapi.dto.solution.*;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.solution.*;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.solution.SecurityValidationUtils;
import com.github.konstantyn111.crashapi.util.solution.SolutionMapperUtil;
import com.github.konstantyn111.crashapi.util.solution.SolutionOperations;
import com.github.konstantyn111.crashapi.util.solution.SolutionUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private static final Logger logger = LoggerFactory.getLogger(SolutionService.class);

    private final SolutionMapper solutionMapper;
    private final SolutionStepMapper solutionStepMapper;
    private final SolutionImageMapper solutionImageMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final SolutionOperations solutionOperations;

    // ==================== 公共接口 ====================

    @Transactional(readOnly = true)
    public SolutionPageDto getPublishedSolutions(Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();

        List<Solution> solutions = solutionMapper.findPublishedSolutions(offset, pageSize);
        int total = solutionMapper.countPublishedSolutions();

        if (solutions.isEmpty()) {
            return SolutionPageDto.fromPage(new PageImpl<>(Collections.emptyList(), pageable, 0));
        }

        List<String> solutionIds = solutions.stream().map(Solution::getId).toList();

        Map<String, List<String>> stepsMap = solutionStepMapper.findStepsBySolutionIds(solutionIds)
                .stream()
                .collect(Collectors.groupingBy(SolutionStep::getSolutionId,
                        Collectors.mapping(SolutionStep::getContent, Collectors.toList())));

        Map<String, List<String>> imagesMap = solutionImageMapper.findImagesBySolutionIds(solutionIds)
                .stream()
                .collect(Collectors.groupingBy(SolutionImage::getSolutionId,
                        Collectors.mapping(SolutionImage::getImageUrl, Collectors.toList())));

        List<SolutionDTO> dtos = solutions.stream()
                .map(sol -> SolutionMapperUtil.toSolutionDTO(
                        sol,
                        stepsMap.getOrDefault(sol.getId(), Collections.emptyList()),
                        imagesMap.getOrDefault(sol.getId(), Collections.emptyList())))
                .toList();

        return SolutionPageDto.fromPage(new PageImpl<>(dtos, pageable, total));
    }

    @Transactional(readOnly = true)
    public RestResponse<SolutionDTO> getSolutionById(String solutionId) {
        Solution solution = solutionMapper.findById(solutionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND, HttpStatus.NOT_FOUND, "解决方案不存在"));

        List<String> steps = solutionStepMapper.findStepsBySolutionId(solutionId)
                .stream().map(SolutionStep::getContent).toList();

        List<String> images = solutionImageMapper.findImagesBySolutionId(solutionId)
                .stream().map(SolutionImage::getImageUrl).toList();

        SolutionDTO dto = SolutionMapperUtil.toSolutionDTO(solution, steps, images);
        return RestResponse.success(dto, "获取解决方案成功");
    }

    @Transactional(readOnly = true)
    public RestResponse<List<CategoryDTO>> getAllCategories() {
        try {
            List<CategoryDTO> dtos = categoryMapper.findAll().stream()
                    .map(SolutionMapperUtil::toCategoryDTO)
                    .toList();
            return RestResponse.success(dtos, "获取分类列表成功");
        } catch (Exception ex) {
            logger.error("获取分类列表失败", ex);
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取分类列表失败: " + ex.getMessage());
        }
    }

    // ==================== 管理员接口 ====================

    @Transactional
    public RestResponse<SolutionDTO> createSolution(SolutionCreateDTO createDTO) {
        try {
            User admin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SolutionMapperUtil.toSolutionEntity(createDTO, admin);
            solutionMapper.insert(solution);

            solutionOperations.createStepsAndImages(solution, createDTO, solutionStepMapper, solutionImageMapper);

            SolutionDTO dto = SolutionMapperUtil.toSolutionDTO(solution, createDTO.getSteps(), Collections.emptyList());
            return RestResponse.success(dto, "解决方案创建成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "创建解决方案失败: " + ex.getMessage());
        }
    }

    @Transactional
    public RestResponse<SolutionDTO> updateSolutionAdmin(String solutionId, SolutionUpdateDTO updateDTO) {
        try {
            User admin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            SecurityValidationUtils.validateSolutionOwnership(solution, admin, "修改");
            validateSolutionStatusForUpdate(solution);

            solutionOperations.updateCore(solution, updateDTO, solutionMapper, solutionStepMapper, solutionImageMapper);

            SolutionDTO dto = SolutionMapperUtil.toSolutionDTO(solution,
                    updateDTO.getSteps(), updateDTO.getExistingImageUrls());
            return RestResponse.success(dto, "解决方案更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "更新解决方案失败: " + ex.getMessage());
        }
    }

    @Transactional
    public RestResponse<Void> deleteSolution(String solutionId) {
        try {
            User admin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            SecurityValidationUtils.validateSolutionOwnership(solution, admin, "删除");
            validateSolutionStatusForDelete(solution);

            solutionOperations.deleteAssociations(solutionId, solutionStepMapper, solutionImageMapper);
            solutionMapper.delete(solutionId);

            return RestResponse.success("解决方案删除成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "删除解决方案失败: " + ex.getMessage());
        }
    }

    @Transactional
    public RestResponse<Void> withdrawSolution(String solutionId) {
        try {
            User admin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            SecurityValidationUtils.validateSolutionOwnership(solution, admin, "撤回");

            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.WITHDRAW, null);
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已撤回为草稿状态");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "撤回解决方案失败: " + ex.getMessage());
        }
    }

    @Transactional
    public RestResponse<Void> submitSolutionForReview(String solutionId) {
        try {
            User admin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            SecurityValidationUtils.validateSolutionOwnership(solution, admin, "提交");

            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.SUBMIT_FOR_REVIEW, null);
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已提交审核");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "提交解决方案审核失败: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getMySolutions(Pageable pageable, String status) {
        try {
            User admin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            List<Solution> solutions = solutionMapper.findByCreator(admin.getId(), status, pageable.getPageSize(), (int) pageable.getOffset());
            long total = solutionMapper.countByCreator(admin.getId(), status);

            List<SolutionDTO> dtos = solutions.stream()
                    .map(sol -> SolutionMapperUtil.toSolutionDTO(sol, null, null))
                    .toList();

            return RestResponse.success(new PageImpl<>(dtos, pageable, total), "获取解决方案列表成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "获取解决方案列表失败: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public RestResponse<SolutionDTO> getSolutionByIdAdmin(String solutionId) {
        try {
            SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            SolutionDTO dto = SolutionMapperUtil.toSolutionDTO(solution, null, null);
            return RestResponse.success(dto, "获取解决方案成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "获取解决方案失败: " + ex.getMessage());
        }
    }

    // ==================== 审核接口 ====================

    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getPendingSolutions(Pageable pageable) {
        try {
            List<Solution> solutions = solutionMapper.findByStatus(SolutionUtils.PENDING_REVIEW, pageable.getPageSize(), (int) pageable.getOffset());
            long total = solutionMapper.countByStatus(SolutionUtils.PENDING_REVIEW);

            List<SolutionDTO> dtos = solutions.stream()
                    .map(sol -> SolutionMapperUtil.toSolutionDTO(sol, null, null))
                    .toList();

            return RestResponse.success(new PageImpl<>(dtos, pageable, total), "获取待审核解决方案成功");
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "获取待审核解决方案失败: " + ex.getMessage());
        }
    }

    @Transactional
    public RestResponse<Void> approveSolution(String solutionId) {
        try {
            User dev = SecurityValidationUtils.validateDeveloperPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            validateSolutionStatusForReview(solution);

            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.APPROVE, null);
            solution.setReviewedBy(dev.getId());
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已批准发布");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.SOLUTION_REVIEW_FAILED, "批准解决方案失败: " + ex.getMessage());
        }
    }

    @Transactional
    public RestResponse<Void> rejectSolution(String solutionId, String reason) {
        try {
            User dev = SecurityValidationUtils.validateDeveloperPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            validateSolutionStatusForReview(solution);

            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.REJECT, reason);
            solution.setReviewedBy(dev.getId());
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已拒绝");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.SOLUTION_REVIEW_FAILED, "拒绝解决方案失败: " + ex.getMessage());
        }
    }

    @Transactional
    public RestResponse<SolutionDTO> updateSolutionDeveloper(String solutionId, SolutionUpdateDTO updateDTO) {
        try {
            SecurityValidationUtils.validateDeveloperPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            if (!SolutionUtils.PUBLISHED.equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST, "只能修改已发布状态的解决方案");
            }

            solutionOperations.updateCore(solution, updateDTO, solutionMapper, solutionStepMapper, solutionImageMapper);
            SolutionDTO dto = SolutionMapperUtil.toSolutionDTO(solution, updateDTO.getSteps(), updateDTO.getExistingImageUrls());
            return RestResponse.success(dto, "解决方案更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR, "更新解决方案失败: " + ex.getMessage());
        }
    }

    // ==================== 状态验证 ====================

    private void validateSolutionStatusForUpdate(Solution solution) {
        if (!SolutionUtils.DRAFT.equals(solution.getStatus()) &&
                !SolutionUtils.PUBLISHED.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                    HttpStatus.BAD_REQUEST, "只能修改草稿或已发布状态的解决方案");
        }
    }

    private void validateSolutionStatusForDelete(Solution solution) {
        if (!SolutionUtils.DRAFT.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                    HttpStatus.BAD_REQUEST, "只能删除草稿状态的解决方案");
        }
    }

    private void validateSolutionStatusForReview(Solution solution) {
        if (!SolutionUtils.PENDING_REVIEW.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION,
                    HttpStatus.BAD_REQUEST, "只能审核待处理状态的解决方案");
        }
    }
}
