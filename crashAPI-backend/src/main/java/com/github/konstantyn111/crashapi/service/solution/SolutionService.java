package com.github.konstantyn111.crashapi.service.solution;

import com.github.konstantyn111.crashapi.dto.solution.CategoryDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.entity.solution.*;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.solution.*;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.solution.SecurityValidationUtils;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 解决方案管理服务
 * <p>
 * 提供解决方案的创建、查询、更新和状态管理等核心业务逻辑。
 * 包含公共接口、管理员接口和开发者接口的功能实现。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SolutionService {

    private static final Logger logger = LoggerFactory.getLogger(SolutionService.class);

    // Mappers
    private final SolutionMapper solutionMapper;
    private final SolutionStepMapper solutionStepMapper;
    private final SolutionImageMapper solutionImageMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    // 工具类
    private final SolutionUtils solutionUtils;
    private final SolutionOperations solutionOperations;

    // ==================== 公共接口 ====================

    /**
     * 分页获取已发布的解决方案
     * <p>
     * 查询状态为PUBLISHED的解决方案，可按分类ID过滤。
     * </p>
     *
     * @param pageable 分页参数
     * @param categoryId 分类ID（可选过滤条件）
     * @return 分页的解决方案列表响应实体
     */
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getPublishedSolutions(Pageable pageable, String categoryId) {
        try {
            List<Solution> solutions = solutionMapper.findPublishedSolutions(
                    SolutionUtils.PUBLISHED,
                    categoryId,
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countPublishedSolutions(
                    SolutionUtils.PUBLISHED,
                    categoryId
            );

            List<SolutionDTO> dtos = solutions.stream()
                    .map(solutionUtils::enrichWithDetails)
                    .collect(Collectors.toList());

            Page<SolutionDTO> page = new PageImpl<>(dtos, pageable, total);
            return RestResponse.success(page, "获取解决方案列表成功");
        } catch (Exception ex) {
            logger.error("获取解决方案列表失败", ex);
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案列表失败: " + ex.getMessage());
        }
    }

    /**
     * 根据ID获取解决方案详情（公开）
     * <p>
     * 仅返回状态为PUBLISHED的解决方案详情。
     * </p>
     *
     * @param solutionId 解决方案ID
     * @return 解决方案详情响应实体
     */
    @Transactional(readOnly = true)
    public RestResponse<SolutionDTO> getSolutionById(String solutionId) {
        try {
            Solution solution = solutionMapper.findPublishedById(solutionId, SolutionUtils.PUBLISHED)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在或未发布"));

            SolutionDTO dto = solutionUtils.enrichWithDetails(solution);
            return RestResponse.success(dto, "获取解决方案成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            logger.error("获取解决方案详情失败: solutionId={}", solutionId, ex);
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 获取所有问题分类
     * <p>
     * 返回所有可用的问题分类列表。
     * </p>
     *
     * @return 分类列表响应实体
     */
    @Transactional(readOnly = true)
    public RestResponse<List<CategoryDTO>> getAllCategories() {
        try {
            List<Category> categories = categoryMapper.findAll();

            List<CategoryDTO> dtos = categories.stream()
                    .map(this::convertToCategoryDTO)
                    .collect(Collectors.toList());

            return RestResponse.success(dtos, "获取分类列表成功");
        } catch (Exception ex) {
            logger.error("获取分类列表失败", ex);
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取分类列表失败: " + ex.getMessage());
        }
    }

    // ==================== 管理员接口 ====================

    /**
     * 创建解决方案
     * <p>
     * 创建新的解决方案草稿（状态为DRAFT）。
     * </p>
     *
     * @param createDTO 解决方案创建数据传输对象
     * @return 创建的解决方案详情响应实体
     */
    @Transactional
    public RestResponse<SolutionDTO> createSolution(SolutionCreateDTO createDTO) {
        try {
            User currentAdmin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Category category = SecurityValidationUtils.validateCategoryExists(categoryMapper, createDTO.getCategoryId());

            Solution solution = createSolutionEntity(createDTO, currentAdmin);
            solutionMapper.insert(solution);

            solutionOperations.createStepsAndImages(
                    solution,
                    createDTO,
                    solutionStepMapper,
                    solutionImageMapper
            );

            SolutionDTO dto = SolutionUtils.convertToDTO(solution);
            return RestResponse.success(dto, "解决方案创建成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "创建解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 更新解决方案（管理员）
     * <p>
     * 更新解决方案基本信息、步骤和图片。
     * </p>
     *
     * @param solutionId 要更新的解决方案ID
     * @param updateDTO 解决方案更新数据传输对象
     * @return 更新后的解决方案详情响应实体
     */
    @Transactional
    public RestResponse<SolutionDTO> updateSolutionAdmin(String solutionId, SolutionUpdateDTO updateDTO) {
        try {
            User currentAdmin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            SecurityValidationUtils.validateSolutionOwnership(solution, currentAdmin, "修改");
            validateSolutionStatusForUpdate(solution);

            solutionOperations.updateCore(
                    solution,
                    updateDTO,
                    solutionMapper,
                    solutionStepMapper,
                    solutionImageMapper
            );

            SolutionDTO dto = SolutionUtils.convertToDTO(solution);
            return RestResponse.success(dto, "解决方案更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 删除解决方案
     * <p>
     * 永久删除解决方案及其关联数据。
     * </p>
     *
     * @param solutionId 要删除的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @Transactional
    public RestResponse<Void> deleteSolution(String solutionId) {
        try {
            User currentAdmin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            SecurityValidationUtils.validateSolutionOwnership(solution, currentAdmin, "删除");
            validateSolutionStatusForDelete(solution);

            solutionOperations.deleteAssociations(solutionId, solutionStepMapper, solutionImageMapper);
            solutionMapper.delete(solutionId);

            return RestResponse.success("解决方案删除成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "删除解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 撤回已发布解决方案
     * <p>
     * 将解决方案状态从PUBLISHED改为DRAFT。
     * </p>
     *
     * @param solutionId 要撤回的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @Transactional
    public RestResponse<Void> withdrawSolution(String solutionId) {
        try {
            User currentAdmin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            SecurityValidationUtils.validateSolutionOwnership(solution, currentAdmin, "撤回");
            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.WITHDRAW, null);
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已撤回为草稿状态");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "撤回解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 提交解决方案审核
     * <p>
     * 将解决方案状态从DRAFT改为PENDING_REVIEW。
     * </p>
     *
     * @param solutionId 要提交审核的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @Transactional
    public RestResponse<Void> submitSolutionForReview(String solutionId) {
        try {
            User currentAdmin = SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            SecurityValidationUtils.validateSolutionOwnership(solution, currentAdmin, "提交");
            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.SUBMIT_FOR_REVIEW, null);
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已提交审核");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "提交解决方案审核失败: " + ex.getMessage());
        }
    }

    /**
     * 分页获取管理员创建的解决方案
     * <p>
     * 查询当前管理员创建的所有解决方案（可按状态过滤）。
     * </p>
     *
     * @param pageable 分页参数
     * @param status 解决方案状态（可选过滤条件）
     * @return 分页的解决方案列表响应实体
     */
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getMySolutions(Pageable pageable, String status) {
        try {
            User currentAdmin = SecurityValidationUtils.validateAdminPermissions(userMapper);

            List<Solution> solutions = solutionMapper.findByCreator(
                    currentAdmin.getId(),
                    status,
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countByCreator(currentAdmin.getId(), status);
            List<SolutionDTO> dtos = SolutionUtils.convertListToDTO(solutions);

            return RestResponse.success(new PageImpl<>(dtos, pageable, total), "获取解决方案列表成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案列表失败: " + ex.getMessage());
        }
    }

    /**
     * 根据ID获取解决方案详情（管理员视图）
     * <p>
     * 返回任意状态的解决方案详情（仅限管理员创建的解决方案）。
     * </p>
     *
     * @param solutionId 解决方案ID
     * @return 解决方案详情响应实体
     */
    @Transactional(readOnly = true)
    public RestResponse<SolutionDTO> getSolutionByIdAdmin(String solutionId) {
        try {
            SecurityValidationUtils.validateAdminPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);
            SolutionDTO dto = SolutionUtils.convertToDTO(solution);
            return RestResponse.success(dto, "获取解决方案成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案失败: " + ex.getMessage());
        }
    }

    // ==================== 开发者接口 ====================

    /**
     * 分页获取待审核的解决方案
     * <p>
     * 查询状态为PENDING_REVIEW的解决方案列表。
     * </p>
     *
     * @param pageable 分页参数
     * @return 分页的解决方案列表响应实体
     */
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getPendingSolutions(Pageable pageable) {
        try {
            List<Solution> solutions = solutionMapper.findByStatus(
                    SolutionUtils.PENDING_REVIEW,
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countByStatus(SolutionUtils.PENDING_REVIEW);
            List<SolutionDTO> dtos = SolutionUtils.convertListToDTO(solutions);

            return RestResponse.success(new PageImpl<>(dtos, pageable, total), "获取待审核解决方案成功");
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取待审核解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 批准解决方案发布
     * <p>
     * 将解决方案状态从PENDING_REVIEW改为PUBLISHED。
     * </p>
     *
     * @param solutionId 要批准的解决方案ID
     * @return 操作结果响应（无数据体）
     */
    @Transactional
    public RestResponse<Void> approveSolution(String solutionId) {
        try {
            User currentDev = SecurityValidationUtils.validateDeveloperPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            validateSolutionStatusForReview(solution);
            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.APPROVE, null);
            solution.setReviewedBy(currentDev.getId());
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已批准发布");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.SOLUTION_REVIEW_FAILED,
                    "批准解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 拒绝解决方案发布
     * <p>
     * 将解决方案状态从PENDING_REVIEW改为DRAFT，可附加拒绝原因。
     * </p>
     *
     * @param solutionId 要拒绝的解决方案ID
     * @param reason 拒绝原因（可选）
     * @return 操作结果响应（无数据体）
     */
    @Transactional
    public RestResponse<Void> rejectSolution(String solutionId, String reason) {
        try {
            User currentDev = SecurityValidationUtils.validateDeveloperPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            validateSolutionStatusForReview(solution);
            SolutionUtils.transitionState(solution, SolutionUtils.StateAction.REJECT, reason);
            solution.setReviewedBy(currentDev.getId());
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已拒绝");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.SOLUTION_REVIEW_FAILED,
                    "拒绝解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 更新已发布的解决方案（开发者）
     * <p>
     * 更新已发布解决方案的基本信息、步骤和图片。
     * </p>
     *
     * @param solutionId 要更新的解决方案ID
     * @param updateDTO 解决方案更新数据传输对象
     * @return 更新后的解决方案详情响应实体
     */
    @Transactional
    public RestResponse<SolutionDTO> updateSolutionDeveloper(String solutionId, SolutionUpdateDTO updateDTO) {
        try {
            SecurityValidationUtils.validateDeveloperPermissions(userMapper);
            Solution solution = SecurityValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            if (!SolutionUtils.PUBLISHED.equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "只能修改已发布状态的解决方案");
            }

            solutionOperations.updateCore(
                    solution,
                    updateDTO,
                    solutionMapper,
                    solutionStepMapper,
                    solutionImageMapper
            );
            SolutionDTO dto = SolutionUtils.convertToDTO(solution);
            return RestResponse.success(dto, "解决方案更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新解决方案失败: " + ex.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 将分类实体转换为DTO
     *
     * @param category 分类实体
     * @return 分类数据传输对象
     */
    private CategoryDTO convertToCategoryDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIcon(category.getIcon());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        return dto;
    }

    /**
     * 创建解决方案实体
     *
     * @param createDTO 创建数据传输对象
     * @param currentAdmin 当前管理员实体
     * @return 初始化的解决方案实体
     */
    private Solution createSolutionEntity(SolutionCreateDTO createDTO, User currentAdmin) {
        Solution solution = new Solution();
        solution.setId(SolutionUtils.generateId());
        solution.setCategoryId(createDTO.getCategoryId());
        solution.setTitle(createDTO.getTitle());
        solution.setDifficulty(createDTO.getDifficulty());
        solution.setVersion(createDTO.getVersion());
        solution.setDescription(createDTO.getDescription());
        solution.setNotes(createDTO.getNotes());
        solution.setStatus(SolutionUtils.DRAFT);
        solution.setCreatedBy(currentAdmin.getId());
        solution.setUpdatedAt(currentAdmin.getCreatedAt());
        return solution;
    }

    /**
     * 验证解决方案状态是否允许更新
     *
     * @param solution 解决方案实体
     * @throws BusinessException 当状态不允许更新时抛出
     */
    private void validateSolutionStatusForUpdate(Solution solution) {
        if (!SolutionUtils.DRAFT.equals(solution.getStatus()) &&
                !SolutionUtils.PUBLISHED.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                    HttpStatus.BAD_REQUEST,
                    "只能修改草稿或已发布状态的解决方案");
        }
    }

    /**
     * 验证解决方案状态是否允许删除
     *
     * @param solution 解决方案实体
     * @throws BusinessException 当状态不允许删除时抛出
     */
    private void validateSolutionStatusForDelete(Solution solution) {
        if (!SolutionUtils.DRAFT.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                    HttpStatus.BAD_REQUEST,
                    "只能删除草稿状态的解决方案");
        }
    }

    /**
     * 验证解决方案状态是否允许审核
     *
     * @param solution 解决方案实体
     * @throws BusinessException 当状态不允许审核时抛出
     */
    private void validateSolutionStatusForReview(Solution solution) {
        if (!SolutionUtils.PENDING_REVIEW.equals(solution.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION,
                    HttpStatus.BAD_REQUEST,
                    "只能审核待处理状态的解决方案");
        }
    }
}