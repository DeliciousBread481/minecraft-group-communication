package com.github.konstantyn111.crashapi.service.admin;

import com.github.konstantyn111.crashapi.dto.*;
import com.github.konstantyn111.crashapi.dto.solution.CategoryDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.entity.*;
import com.github.konstantyn111.crashapi.entity.solution.Category;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.entity.solution.SolutionImage;
import com.github.konstantyn111.crashapi.entity.solution.SolutionStep;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.mapper.*;
import com.github.konstantyn111.crashapi.mapper.solution.CategoryMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;
import com.github.konstantyn111.crashapi.util.RestResponse;
import com.github.konstantyn111.crashapi.util.ErrorCode;
import com.github.konstantyn111.crashapi.util.SolutionConvertUtil;
import com.github.konstantyn111.crashapi.util.UserConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;
    private final SolutionMapper solutionMapper;
    private final SolutionStepMapper solutionStepMapper;
    private final SolutionImageMapper solutionImageMapper;
    private final CategoryMapper categoryMapper;

    // 管理员角色常量
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    /**
     * 根据用户ID获取用户信息
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<UserInfo> getUserInfoById(Long userId) {
        try {
            // 验证管理员权限
            validateAdminPermissions();

            User user = userMapper.findByIdWithRoles(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "用户不存在"));

            return RestResponse.success(UserConvertUtil.convertToUserInfo(user), "获取用户信息成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * 创建解决方案（管理员）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<SolutionDTO> createSolution(SolutionCreateDTO createDTO) {
        try {
            // 验证管理员权限
            User currentAdmin = validateAdminPermissions();

            // 验证分类是否存在
            Category category = categoryMapper.findById(createDTO.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "问题分类不存在"));

            // 创建解决方案
            Solution solution = new Solution();
            solution.setId(generateSolutionId());
            solution.setCategoryId(createDTO.getCategoryId());
            solution.setTitle(createDTO.getTitle());
            solution.setDifficulty(createDTO.getDifficulty());
            solution.setVersion(createDTO.getVersion());
            solution.setDescription(createDTO.getDescription());
            solution.setNotes(createDTO.getNotes());
            solution.setStatus("草稿"); // 初始状态为草稿
            solution.setCreatedBy(currentAdmin.getId());
            solution.setCreatedAt(LocalDateTime.now());
            solution.setUpdatedAt(LocalDateTime.now());

            solutionMapper.insert(solution);

            // 添加步骤
            if (createDTO.getSteps() != null && !createDTO.getSteps().isEmpty()) {
                for (int i = 0; i < createDTO.getSteps().size(); i++) {
                    SolutionStep step = new SolutionStep();
                    step.setSolutionId(solution.getId());
                    step.setStepOrder(i + 1);
                    step.setContent(createDTO.getSteps().get(i));
                    solutionStepMapper.insert(step);
                }
            }

            // 添加图片
            if (createDTO.getImageUrls() != null && !createDTO.getImageUrls().isEmpty()) {
                for (int i = 0; i < createDTO.getImageUrls().size(); i++) {
                    SolutionImage image = new SolutionImage();
                    image.setSolutionId(solution.getId());
                    image.setImageOrder(i + 1);
                    image.setImageUrl(createDTO.getImageUrls().get(i));
                    solutionImageMapper.insert(image);
                }
            }

            // 使用SolutionConvertUtil转换解决方案
            SolutionDTO dto = SolutionConvertUtil.convertToSolutionDTO(solution);
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
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<SolutionDTO> updateSolution(String solutionId, SolutionUpdateDTO updateDTO) {
        try {
            // 验证管理员权限
            User currentAdmin = validateAdminPermissions();

            // 获取现有解决方案
            Solution solution = solutionMapper.findById(solutionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在"));

            // 验证解决方案创建者
            if (!solution.getCreatedBy().equals(currentAdmin.getId())) {
                throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                        HttpStatus.FORBIDDEN,
                        "只能修改自己创建的解决方案");
            }

            // 验证状态（只能修改草稿状态）
            if (!"草稿".equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "只能修改草稿状态的解决方案");
            }

            // 更新解决方案
            solution.setTitle(updateDTO.getTitle());
            solution.setDifficulty(updateDTO.getDifficulty());
            solution.setVersion(updateDTO.getVersion());
            solution.setDescription(updateDTO.getDescription());
            solution.setNotes(updateDTO.getNotes());
            solution.setUpdatedAt(LocalDateTime.now());

            solutionMapper.update(solution);

            // 删除现有步骤和图片
            solutionStepMapper.deleteBySolutionId(solutionId);
            solutionImageMapper.deleteBySolutionId(solutionId);

            // 添加新步骤
            if (updateDTO.getSteps() != null && !updateDTO.getSteps().isEmpty()) {
                for (int i = 0; i < updateDTO.getSteps().size(); i++) {
                    SolutionStep step = new SolutionStep();
                    step.setSolutionId(solutionId);
                    step.setStepOrder(i + 1);
                    step.setContent(updateDTO.getSteps().get(i));
                    solutionStepMapper.insert(step);
                }
            }

            // 添加新图片
            if (updateDTO.getImageUrls() != null && !updateDTO.getImageUrls().isEmpty()) {
                for (int i = 0; i < updateDTO.getImageUrls().size(); i++) {
                    SolutionImage image = new SolutionImage();
                    image.setSolutionId(solutionId);
                    image.setImageOrder(i + 1);
                    image.setImageUrl(updateDTO.getImageUrls().get(i));
                    solutionImageMapper.insert(image);
                }
            }

            // 使用SolutionConvertUtil转换解决方案
            SolutionDTO dto = SolutionConvertUtil.convertToSolutionDTO(solution);
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
     * 删除解决方案（管理员）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<Void> deleteSolution(String solutionId) {
        try {
            // 验证管理员权限
            User currentAdmin = validateAdminPermissions();

            // 获取解决方案
            Solution solution = solutionMapper.findById(solutionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在"));

            // 验证解决方案创建者
            if (!solution.getCreatedBy().equals(currentAdmin.getId())) {
                throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                        HttpStatus.FORBIDDEN,
                        "只能删除自己创建的解决方案");
            }

            // 验证状态（只能删除草稿状态）
            if (!"草稿".equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "只能删除草稿状态的解决方案");
            }

            // 删除解决方案及相关数据
            solutionStepMapper.deleteBySolutionId(solutionId);
            solutionImageMapper.deleteBySolutionId(solutionId);
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
     * 提交解决方案审核（管理员）
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<Void> submitSolutionForReview(String solutionId) {
        try {
            // 验证管理员权限
            User currentAdmin = validateAdminPermissions();

            // 获取解决方案
            Solution solution = solutionMapper.findById(solutionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在"));

            // 验证解决方案创建者
            if (!solution.getCreatedBy().equals(currentAdmin.getId())) {
                throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                        HttpStatus.FORBIDDEN,
                        "只能提交自己创建的解决方案");
            }

            // 验证状态（只能从草稿状态提交）
            if (!"草稿".equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "只能提交草稿状态的解决方案");
            }

            // 更新状态为待审核
            solution.setStatus("待审核");
            solution.setUpdatedAt(LocalDateTime.now());
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
     * 获取管理员创建的解决方案列表
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getMySolutions(Pageable pageable, String status) {
        try {
            // 验证管理员权限
            User currentAdmin = validateAdminPermissions();

            // 查询解决方案
            List<Solution> solutions = solutionMapper.findByCreator(
                    currentAdmin.getId(),
                    status,
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countByCreator(currentAdmin.getId(), status);

            // 使用SolutionConvertUtil转换解决方案列表
            List<SolutionDTO> dtos = SolutionConvertUtil.convertToSolutionDTOList(solutions);

            Page<SolutionDTO> page = new PageImpl<>(dtos, pageable, total);
            return RestResponse.success(page, "获取解决方案列表成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案列表失败: " + ex.getMessage());
        }
    }

    /**
     * 获取解决方案详情
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<SolutionDTO> getSolutionById(String solutionId) {
        try {
            // 验证管理员权限
            validateAdminPermissions();

            Solution solution = solutionMapper.findById(solutionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SOLUTION_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "解决方案不存在"));

            // 使用SolutionConvertUtil转换解决方案
            SolutionDTO dto = SolutionConvertUtil.convertToSolutionDTO(solution);
            return RestResponse.success(dto, "获取解决方案成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * 获取所有问题分类
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<List<CategoryDTO>> getAllCategories() {
        try {
            // 验证管理员权限
            validateAdminPermissions();

            List<Category> categories = categoryMapper.findAll();

            List<CategoryDTO> dtos = categories.stream()
                    .map(category -> {
                        CategoryDTO dto = new CategoryDTO();
                        dto.setId(category.getId());
                        dto.setName(category.getName());
                        dto.setIcon(category.getIcon());
                        dto.setDescription(category.getDescription());
                        dto.setColor(category.getColor());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return RestResponse.success(dtos, "获取分类列表成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取分类列表失败: " + ex.getMessage());
        }
    }

    // ============= 私有方法 ============= //

    /**
     * 验证当前用户是否为管理员并返回用户实体
     */
    private User validateAdminPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "当前用户不存在"));

        // 检查管理员权限
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> ADMIN_ROLE.equals(role.getName()));

        if (!isAdmin) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED,
                    HttpStatus.FORBIDDEN,
                    "只有管理员能执行此操作");
        }

        return currentUser;
    }

    /**
     * 生成解决方案ID
     */
    private String generateSolutionId() {
        return "s" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}